import asyncio
import random
import string
import time
import aiohttp

URL = "http://localhost:8080/api/restaurants"
TOTAL = 10_000
BATCH_SIZE = 100
CONCURRENCY = 20

CUISINES = [
    "Indian", "Italian", "Chinese", "Mexican", "Japanese",
    "Thai", "Mediterranean", "American", "French", "Korean",
    "Vietnamese", "Greek", "Spanish", "Turkish", "Lebanese",
    "BBQ", "Seafood", "Vegan", "Fast Food", "Dessert",
]

NAME_PARTS_1 = [
    "Spice", "Golden", "Royal", "Urban", "Coastal", "Garden", "Red",
    "Blue", "Green", "Saffron", "Cardamom", "Cinnamon", "Tandoori",
    "Sizzling", "Fresh", "Hot", "The", "New", "Old", "Grand",
    "Mumbai", "Delhi", "Bombay", "Bengal", "Hyderabad", "Lucknow",
    "Pizza", "Taco", "Sushi", "Wok", "Grill", "Curry", "Biryani",
    "Star", "Moon", "Sun", "Cloud", "River", "Mountain", "Ocean",
]

NAME_PARTS_2 = [
    "Garden", "Palace", "Kitchen", "House", "Bistro", "Cafe",
    "Corner", "Hub", "Point", "Plaza", "Express", "Den",
    "Terrace", "Place", "Zone", "Leaf", "Plate", "Bowl",
    "Dragon", "Tiger", "Elephant", "Pheonix", "Eagle", "Lion",
    "Bay", "Harbor", "Shore", "Creek", "Brook", "Vista",
]

STREETS = [
    "Main", "Park", "Lake", "Hill", "Oak", "Pine", "Maple",
    "Cedar", "Elm", "Spring", "Valley", "River", "Sunset",
    "Sunrise", "Garden", "Church", "High", "Broad", "King",
    "Queen", "Station", "Market", "Temple", "MG Road", "Nehru",
]

CITIES = [
    "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata",
    "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow",
]

def generate_restaurant(idx):
    name = f"{random.choice(NAME_PARTS_1)} {random.choice(NAME_PARTS_2)}"
    if random.random() > 0.5:
        name += f" {random.choice(NAME_PARTS_2)}"
    num = random.randint(1, 999)
    street = random.choice(STREETS)
    city = random.choice(CITIES)
    pin = random.randint(100000, 999999)
    address = f"{num}, {street} Street, {city} - {pin}"
    phone = f"+91-{random.randint(6000000000, 9999999999)}"
    return {
        "name": name,
        "address": address,
        "cuisineType": random.choice(CUISINES),
        "rating": round(random.uniform(1.0, 5.0), 1),
        "phone": phone,
        "isActive": random.random() > 0.1,
    }

async def post_one(session, sem, data, idx):
    async with sem:
        for attempt in range(3):
            try:
                async with session.post(URL, json=data) as resp:
                    if resp.status == 201:
                        return True
                    await resp.text()
            except Exception:
                pass
            await asyncio.sleep(0.1 * (attempt + 1))
        print(f"  FAILED record #{idx}: {data['name']}")
        return False

async def main():
    print(f"Generating {TOTAL} restaurant records...")
    records = [generate_restaurant(i) for i in range(TOTAL)]

    sem = asyncio.Semaphore(CONCURRENCY)
    success = 0
    failed = 0

    print(f"POSTing to {URL} (concurrency={CONCURRENCY}, batch={BATCH_SIZE})...")
    start = time.time()

    connector = aiohttp.TCPConnector(limit=CONCURRENCY)
    async with aiohttp.ClientSession(connector=connector) as session:
        for batch_start in range(0, TOTAL, BATCH_SIZE):
            batch = records[batch_start:batch_start + BATCH_SIZE]
            tasks = [post_one(session, sem, rec, batch_start + i) for i, rec in enumerate(batch)]
            results = await asyncio.gather(*tasks)
            success += sum(results)
            failed += sum(1 for r in results if not r)
            done = batch_start + len(batch)
            pct = done / TOTAL * 100
            elapsed = time.time() - start
            rate = done / elapsed if elapsed > 0 else 0
            print(f"  [{done:>6}/{TOTAL}] {pct:5.1f}%  |  {rate:.0f} rec/s  |  ok={success} fail={failed}")

    elapsed = time.time() - start
    print(f"\nDone in {elapsed:.1f}s — success={success}, failed={failed}, rate={TOTAL/elapsed:.0f} rec/s")

if __name__ == "__main__":
    asyncio.run(main())
