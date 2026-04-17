#!/usr/bin/env python3
"""
Generate all resource files for the Historic Bombs mod:
- 60 block textures (16x16 PNGs)
- 60 blockstate JSONs
- 60 block model JSONs
- 60 item model JSONs
- 60 loot table JSONs
- Crafting recipes
- Language file (en_us.json)
"""

import json
import os
import struct
import zlib
from pathlib import Path

MOD_ID = "historic_bombs"
BASE = Path("src/main/resources")
ASSETS = BASE / "assets" / MOD_ID
DATA = BASE / "data" / MOD_ID

# Bomb data: (registry_name, display_name, yield_kt, country, year, category, description)
# category: "thermonuclear", "fission", "conventional", "thermobaric", "dnu"
BOMBS = [
    ("tsar_bomba", "Tsar Bomba (AN602)", 50000, "USSR", 1961, "thermonuclear", "Largest nuclear weapon ever detonated"),
    ("test_219", "Test 219", 24200, "USSR", 1962, "thermonuclear", "Second largest Soviet test"),
    ("test_147", "Test 147", 21100, "USSR", 1962, "thermonuclear", "Third largest nuclear test ever"),
    ("test_173", "Test 173", 19100, "USSR", 1962, "thermonuclear", "Fourth largest, Novaya Zemlya"),
    ("castle_bravo", "Castle Bravo", 15000, "USA", 1954, "thermonuclear", "Largest US test"),
    ("castle_yankee", "Castle Yankee", 13500, "USA", 1954, "thermonuclear", "Second largest US test"),
    ("test_95", "Test 95", 12500, "USSR", 1961, "thermonuclear", "Part of the 1961 test series"),
    ("castle_romeo", "Castle Romeo", 11000, "USA", 1954, "thermonuclear", "First barge test"),
    ("ivy_mike", "Ivy Mike", 10400, "USA", 1952, "thermonuclear", "First hydrogen bomb test"),
    ("test_174", "Test 174", 10000, "USSR", 1962, "thermonuclear", "Estimated >10 Mt"),
    ("hardtack_poplar", "Hardtack Poplar", 9300, "USA", 1958, "thermonuclear", "Operation Hardtack I"),
    ("hardtack_oak", "Hardtack Oak", 8900, "USA", 1958, "thermonuclear", "Barge shot at Enewetak"),
    ("dominic_housatonic", "Dominic Housatonic", 8300, "USA", 1962, "thermonuclear", "Operation Dominic"),
    ("castle_union", "Castle Union", 6900, "USA", 1954, "thermonuclear", "Operation Castle series"),
    ("redwing_tewa", "Redwing Tewa", 5000, "USA", 1956, "thermonuclear", "Operation Redwing"),
    ("redwing_navajo", "Redwing Navajo", 4500, "USA", 1956, "thermonuclear", "Surface burst"),
    ("redwing_zuni", "Redwing Zuni", 3530, "USA", 1956, "thermonuclear", "Bikini Atoll"),
    ("redwing_cherokee", "Redwing Cherokee", 3800, "USA", 1956, "thermonuclear", "First airdropped thermonuclear"),
    ("rds_37", "RDS-37", 3000, "USSR", 1955, "thermonuclear", "First Soviet H-bomb"),
    ("b41", "B41 (Mk-41)", 25000, "USA", 1960, "thermonuclear", "Highest yield US weapon deployed"),
    ("castle_nectar", "Castle Nectar", 1690, "USA", 1954, "thermonuclear", "Operation Castle"),
    ("starfish_prime", "Starfish Prime", 1450, "USA", 1962, "thermonuclear", "High altitude test"),
    ("dominic_sedan", "Dominic Sedan", 104, "USA", 1962, "thermonuclear", "Cratering experiment"),
    ("rds_6s", "RDS-6s (Joe 4)", 400, "USSR", 1953, "thermonuclear", "First Soviet thermonuclear"),
    ("grapple_y", "Grapple Y", 3000, "UK", 1958, "thermonuclear", "Largest British test"),
    ("canopus", "Canopus", 2600, "France", 1968, "thermonuclear", "First French thermonuclear"),
    ("test_no_6", "Test No. 6", 4000, "China", 1976, "thermonuclear", "Largest Chinese test"),
    ("punggye_ri", "Punggye-ri (Test 6)", 250, "North Korea", 2017, "thermonuclear", "Largest NK test"),
    ("hardtack_umbrella", "Hardtack Umbrella", 8, "USA", 1958, "fission", "Underwater nuclear test"),
    ("orange_herald", "Orange Herald", 720, "UK", 1957, "fission", "Powerful fission-only bomb"),
    ("little_boy", "Little Boy", 15, "USA", 1945, "fission", "Hiroshima"),
    ("fat_man", "Fat Man", 21, "USA", 1945, "fission", "Nagasaki"),
    ("trinity", "Trinity", 19, "USA", 1945, "fission", "First nuclear test ever"),
    ("rds_1", "RDS-1 (Joe 1)", 22, "USSR", 1949, "fission", "First Soviet nuclear test"),
    ("smiling_buddha", "Smiling Buddha", 12, "India", 1974, "fission", "India's first test"),
    ("chagai_i", "Chagai-I", 40, "Pakistan", 1998, "fission", "Pakistan's first test"),
    ("davy_crockett", "Davy Crockett (W54)", 0.02, "USA", 1962, "fission", "Smallest nuclear weapon"),
    ("foab", "Father of All Bombs (FOAB)", 0.044, "Russia", 2007, "thermobaric", "Thermobaric weapon"),
    ("moab", "GBU-43/B MOAB", 0.011, "USA", 2003, "conventional", "Mother Of All Bombs"),
    ("daisy_cutter", "BLU-82 Daisy Cutter", 0.006, "USA", 1970, "conventional", "15,000 lb bomb"),
    ("gbu_57_mop", "GBU-57 MOP", 0.003, "USA", 2011, "conventional", "Massive Ordnance Penetrator"),
    ("grand_slam", "Grand Slam", 0.0065, "UK", 1945, "conventional", "22,000 lb earthquake bomb"),
    ("tallboy", "Tallboy", 0.0032, "UK", 1944, "conventional", "12,000 lb earthquake bomb"),
    ("t12_cloudmaker", "T-12 Cloudmaker", 0.0086, "USA", 1948, "conventional", "44,000 lb bomb"),
    ("gbu_28", "GBU-28 Bunker Buster", 0.00063, "USA", 1991, "conventional", "Laser guided bunker buster"),
    ("sc_2500_max", "SC 2500 Max", 0.0017, "Germany", 1940, "conventional", "Luftwaffe GP bomb"),
    ("mk_84", "Mk 84 (JDAM)", 0.000945, "USA", 1965, "conventional", "Standard 2,000 lb bomb"),
    ("fab_9000", "FAB-9000", 0.007, "USSR", 1954, "conventional", "9,000 kg Soviet bomb"),
    ("fab_3000", "FAB-3000", 0.0014, "Russia", 1954, "conventional", "3,000 kg bomb"),
    ("fritz_x", "Fritz X", 0.0003, "Germany", 1943, "conventional", "First precision guided munition"),
]

# Top 10 for DNU variants
DNU_BOMBS = [
    ("tsar_bomba_dnu", "Tsar Bomba", 50000, "USSR"),
    ("test_219_dnu", "Test 219", 24200, "USSR"),
    ("test_147_dnu", "Test 147", 21100, "USSR"),
    ("test_173_dnu", "Test 173", 19100, "USSR"),
    ("castle_bravo_dnu", "Castle Bravo", 15000, "USA"),
    ("castle_yankee_dnu", "Castle Yankee", 13500, "USA"),
    ("test_95_dnu", "Test 95", 12500, "USSR"),
    ("castle_romeo_dnu", "Castle Romeo", 11000, "USA"),
    ("ivy_mike_dnu", "Ivy Mike", 10400, "USA"),
    ("test_174_dnu", "Test 174", 10000, "USSR"),
]

# Category colors (R, G, B)
CATEGORY_COLORS = {
    "thermonuclear": (57, 255, 20),    # #39FF14
    "fission":       (255, 179, 0),    # #FFB300
    "conventional":  (194, 163, 102),  # #C2A366
    "thermobaric":   (220, 20, 60),    # #DC143C
    "dnu":           (255, 0, 0),      # #FF0000
}

# Country flag stripe colors (top to bottom for the lower portion of the texture)
COUNTRY_FLAGS = {
    "USA":          [(200, 30, 30), (255, 255, 255), (30, 30, 200)],
    "USSR":         [(200, 30, 30), (30, 30, 200), (255, 255, 255)],
    "Russia":       [(255, 255, 255), (30, 30, 200), (200, 30, 30)],
    "UK":           [(200, 30, 30), (255, 255, 255), (30, 30, 200)],
    "France":       [(30, 30, 200), (255, 255, 255), (200, 30, 30)],
    "China":        [(200, 30, 30), (200, 30, 30), (255, 215, 0)],
    "India":        [(255, 153, 51), (255, 255, 255), (19, 136, 8)],
    "Pakistan":     [(0, 100, 0), (255, 255, 255), (0, 100, 0)],
    "North Korea":  [(30, 30, 200), (200, 30, 30), (30, 30, 200)],
    "Germany":      [(0, 0, 0), (200, 30, 30), (255, 215, 0)],
}

# Simple 3x5 pixel font for stencil text (only uppercase + digits + some special chars)
STENCIL_FONT = {
    'A': ["010", "101", "111", "101", "101"],
    'B': ["110", "101", "110", "101", "110"],
    'C': ["011", "100", "100", "100", "011"],
    'D': ["110", "101", "101", "101", "110"],
    'E': ["111", "100", "110", "100", "111"],
    'F': ["111", "100", "110", "100", "100"],
    'G': ["011", "100", "101", "101", "011"],
    'H': ["101", "101", "111", "101", "101"],
    'I': ["111", "010", "010", "010", "111"],
    'J': ["001", "001", "001", "101", "010"],
    'K': ["101", "110", "100", "110", "101"],
    'L': ["100", "100", "100", "100", "111"],
    'M': ["101", "111", "111", "101", "101"],
    'N': ["101", "111", "111", "111", "101"],
    'O': ["010", "101", "101", "101", "010"],
    'P': ["110", "101", "110", "100", "100"],
    'Q': ["010", "101", "101", "111", "011"],
    'R': ["110", "101", "110", "101", "101"],
    'S': ["011", "100", "010", "001", "110"],
    'T': ["111", "010", "010", "010", "010"],
    'U': ["101", "101", "101", "101", "010"],
    'V': ["101", "101", "101", "010", "010"],
    'W': ["101", "101", "111", "111", "101"],
    'X': ["101", "101", "010", "101", "101"],
    'Y': ["101", "101", "010", "010", "010"],
    'Z': ["111", "001", "010", "100", "111"],
    '0': ["010", "101", "101", "101", "010"],
    '1': ["010", "110", "010", "010", "111"],
    '2': ["110", "001", "010", "100", "111"],
    '3': ["110", "001", "010", "001", "110"],
    '4': ["101", "101", "111", "001", "001"],
    '5': ["111", "100", "110", "001", "110"],
    '6': ["011", "100", "110", "101", "010"],
    '7': ["111", "001", "010", "010", "010"],
    '8': ["010", "101", "010", "101", "010"],
    '9': ["010", "101", "011", "001", "110"],
    '-': ["000", "000", "111", "000", "000"],
    '(': ["010", "100", "100", "100", "010"],
    ')': ["010", "001", "001", "001", "010"],
    '.': ["000", "000", "000", "000", "010"],
    ' ': ["000", "000", "000", "000", "000"],
    '/': ["001", "001", "010", "100", "100"],
}


def create_png(pixels, width=16, height=16):
    """Create a minimal PNG file from pixel data (list of (r,g,b) tuples)."""
    def make_chunk(chunk_type, data):
        chunk = chunk_type + data
        crc = struct.pack('>I', zlib.crc32(chunk) & 0xFFFFFFFF)
        return struct.pack('>I', len(data)) + chunk + crc

    # PNG signature
    sig = b'\x89PNG\r\n\x1a\n'

    # IHDR
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0)  # 8-bit RGB
    ihdr = make_chunk(b'IHDR', ihdr_data)

    # IDAT
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'  # filter: none
        for x in range(width):
            r, g, b = pixels[y * width + x]
            raw_data += struct.pack('BBB', r, g, b)

    compressed = zlib.compress(raw_data)
    idat = make_chunk(b'IDAT', compressed)

    # IEND
    iend = make_chunk(b'IEND', b'')

    return sig + ihdr + idat + iend


def render_text_on_row(pixels, text, y_start, width, text_color, bg_color):
    """Render stencil text centered on a row of pixels."""
    text = text.upper()
    # Calculate text width
    char_width = 4  # 3px char + 1px spacing
    total_width = len(text) * char_width - 1
    if total_width > width:
        # Truncate text to fit
        max_chars = width // char_width
        text = text[:max_chars]
        total_width = len(text) * char_width - 1

    x_start = max(0, (width - total_width) // 2)

    for ci, char in enumerate(text):
        glyph = STENCIL_FONT.get(char, STENCIL_FONT.get(' '))
        if glyph is None:
            continue
        for gy, row in enumerate(glyph):
            py = y_start + gy
            if py >= 16:
                break
            for gx, bit in enumerate(row):
                px = x_start + ci * char_width + gx
                if px < width and bit == '1':
                    pixels[py * width + px] = text_color


def generate_block_texture(name, category, country):
    """Generate a 16x16 block texture for a bomb."""
    width, height = 16, 16
    cat_color = CATEGORY_COLORS.get(category, (128, 128, 128))
    flag_colors = COUNTRY_FLAGS.get(country, [(128, 128, 128)] * 3)

    if category == "dnu":
        # Hazard stripe pattern: alternating red and black
        pixels = []
        for y in range(height):
            for x in range(width):
                if y < 2 or y >= 14:
                    # Hazard stripes on top and bottom
                    if (x + y) % 4 < 2:
                        pixels.append((255, 0, 0))
                    else:
                        pixels.append((0, 0, 0))
                elif y < 8:
                    # DNU text area - dark red background
                    pixels.append((100, 0, 0))
                else:
                    # Flag area
                    stripe_idx = min((y - 8) * len(flag_colors) // 6, len(flag_colors) - 1)
                    pixels.append(flag_colors[stripe_idx])
        # Render "DNU" text
        render_text_on_row(pixels, "DNU", 3, width, (255, 255, 255), (100, 0, 0))
    else:
        pixels = []
        for y in range(height):
            for x in range(width):
                if y < 2:
                    # Top border - category color
                    pixels.append(cat_color)
                elif y < 9:
                    # Text area - slightly darker category color
                    dark = tuple(max(0, c - 40) for c in cat_color)
                    pixels.append(dark)
                elif y < 10:
                    # Divider line
                    pixels.append((60, 60, 60))
                else:
                    # Flag stripes
                    stripe_idx = min((y - 10) * len(flag_colors) // 6, len(flag_colors) - 1)
                    pixels.append(flag_colors[stripe_idx])

        # Generate short label from name
        label = name.replace("_", " ").upper()
        # Truncate to fit - max ~4 chars at this resolution
        words = label.split()
        if len(words) > 1:
            # Use initials or abbreviation
            short = ""
            for w in words:
                if len(short) + len(w) <= 4:
                    short += w[0]
                else:
                    break
            if len(short) < 2:
                short = words[0][:4]
            label = short
        else:
            label = label[:4]

        render_text_on_row(pixels, label, 3, width, (255, 255, 255), cat_color)

    return create_png(pixels, width, height)


def generate_top_texture(category):
    """Generate the top face texture (detonator circle)."""
    width, height = 16, 16
    cat_color = CATEGORY_COLORS.get(category, (128, 128, 128))

    if category == "dnu":
        bg = (100, 0, 0)
    else:
        bg = tuple(max(0, c - 40) for c in cat_color)

    pixels = []
    for y in range(height):
        for x in range(width):
            # Circle pattern for detonator
            dx = x - 7.5
            dy = y - 7.5
            dist = (dx * dx + dy * dy) ** 0.5
            if dist < 3:
                pixels.append((60, 60, 60))  # dark center
            elif dist < 4:
                pixels.append((180, 180, 180))  # ring
            elif dist < 5:
                pixels.append((100, 100, 100))  # outer ring
            else:
                pixels.append(bg)

    return create_png(pixels, width, height)


def generate_bottom_texture(category):
    """Generate the bottom face texture (solid color)."""
    width, height = 16, 16
    cat_color = CATEGORY_COLORS.get(category, (128, 128, 128))

    if category == "dnu":
        pixels = [(100, 0, 0)] * (width * height)
    else:
        pixels = [cat_color] * (width * height)

    return create_png(pixels, width, height)


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    with open(path, 'w') as f:
        json.dump(data, f, indent=2)


def generate_blockstate(name):
    model = {"model": f"{MOD_ID}:block/{name}"}
    return {
        "variants": {
            "unstable=false": model,
            "unstable=true": model
        }
    }


def generate_block_model(name):
    return {
        "parent": "minecraft:block/cube",
        "textures": {
            "up": f"{MOD_ID}:block/{name}_top",
            "down": f"{MOD_ID}:block/{name}_bottom",
            "north": f"{MOD_ID}:block/{name}_side",
            "south": f"{MOD_ID}:block/{name}_side",
            "east": f"{MOD_ID}:block/{name}_side",
            "west": f"{MOD_ID}:block/{name}_side",
            "particle": f"{MOD_ID}:block/{name}_side"
        }
    }


def generate_item_model(name):
    return {
        "parent": f"{MOD_ID}:block/{name}"
    }


def generate_item_definition(name):
    """Generate 1.21.4-style item definition (assets/<modid>/items/<name>.json)"""
    return {
        "model": {
            "type": "minecraft:model",
            "model": f"{MOD_ID}:block/{name}"
        }
    }


def generate_loot_table(name):
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": f"{MOD_ID}:{name}"
                    }
                ],
                "conditions": [
                    {
                        "condition": "minecraft:survives_explosion"
                    }
                ]
            }
        ]
    }


def generate_lang():
    lang = {}
    for name, display_name, *_ in BOMBS:
        lang[f"block.{MOD_ID}.{name}"] = display_name
        lang[f"item.{MOD_ID}.{name}"] = display_name
    for name, display_name, *_ in DNU_BOMBS:
        dnu_name = f"{display_name} \u26A0 DO NOT USE \u26A0"
        lang[f"block.{MOD_ID}.{name}"] = dnu_name
        lang[f"item.{MOD_ID}.{name}"] = dnu_name
    lang[f"itemGroup.{MOD_ID}.historic_bombs"] = "Historic Bombs"
    return lang


def generate_crafting_recipes():
    """Generate crafting recipes based on the tiered system."""
    recipes = {}

    for name, display_name, yield_kt, country, year, category, desc in BOMBS:
        if category == "dnu":
            continue  # DNU variants are creative-only

        if category == "conventional":
            # Conventional: 4 TNT + 4 Iron Ingots + 1 Gunpowder
            recipes[name] = {
                "type": "minecraft:crafting_shaped",
                "pattern": ["TIT", "IGI", "TIT"],
                "key": {
                    "T": {"item": "minecraft:tnt"},
                    "I": {"item": "minecraft:iron_ingot"},
                    "G": {"item": "minecraft:gunpowder"}
                },
                "result": {"id": f"{MOD_ID}:{name}", "count": 1}
            }
        elif category == "thermobaric":
            # Thermobaric: 4 TNT + 4 Blaze Powder + 1 Fire Charge
            recipes[name] = {
                "type": "minecraft:crafting_shaped",
                "pattern": ["TBT", "BFB", "TBT"],
                "key": {
                    "T": {"item": "minecraft:tnt"},
                    "B": {"item": "minecraft:blaze_powder"},
                    "F": {"item": "minecraft:fire_charge"}
                },
                "result": {"id": f"{MOD_ID}:{name}", "count": 1}
            }
        elif yield_kt < 100:
            # Small nuclear: 8 TNT + 1 Nether Star
            recipes[name] = {
                "type": "minecraft:crafting_shaped",
                "pattern": ["TTT", "TNT", "TTT"],
                "key": {
                    "T": {"item": "minecraft:tnt"},
                    "N": {"item": "minecraft:nether_star"}
                },
                "result": {"id": f"{MOD_ID}:{name}", "count": 1}
            }
        elif yield_kt < 5000:
            # Medium nuclear: 4 TNT + 4 Diamonds + 1 Nether Star
            recipes[name] = {
                "type": "minecraft:crafting_shaped",
                "pattern": ["TDT", "DNT", "TDT"],
                "key": {
                    "T": {"item": "minecraft:tnt"},
                    "D": {"item": "minecraft:diamond"},
                    "N": {"item": "minecraft:nether_star"}
                },
                "result": {"id": f"{MOD_ID}:{name}", "count": 1}
            }
        else:
            # Large nuclear: 4 TNT + 4 Netherite Ingots + 1 Nether Star
            recipes[name] = {
                "type": "minecraft:crafting_shaped",
                "pattern": ["TNT", "NAN", "TNT"],
                "key": {
                    "T": {"item": "minecraft:tnt"},
                    "N": {"item": "minecraft:netherite_ingot"},
                    "A": {"item": "minecraft:nether_star"}
                },
                "result": {"id": f"{MOD_ID}:{name}", "count": 1}
            }

    return recipes


def main():
    print("Generating Historic Bombs mod resources...")

    # Create directories
    tex_dir = ASSETS / "textures" / "block"
    blockstate_dir = ASSETS / "blockstates"
    block_model_dir = ASSETS / "models" / "block"
    item_model_dir = ASSETS / "models" / "item"
    item_def_dir = ASSETS / "items"
    loot_dir = DATA / "loot_table" / "blocks"
    recipe_dir = DATA / "recipe"
    lang_dir = ASSETS / "lang"

    for d in [tex_dir, blockstate_dir, block_model_dir, item_model_dir, item_def_dir, loot_dir, recipe_dir, lang_dir]:
        d.mkdir(parents=True, exist_ok=True)

    # Generate unique top/bottom textures per category
    categories_seen = set()
    for cat_name in CATEGORY_COLORS:
        top_png = generate_top_texture(cat_name)
        bottom_png = generate_bottom_texture(cat_name)

    # Process standard bombs
    for name, display_name, yield_kt, country, year, category, desc in BOMBS:
        print(f"  Generating: {name}")

        # Side texture
        side_png = generate_block_texture(name, category, country)
        with open(tex_dir / f"{name}_side.png", 'wb') as f:
            f.write(side_png)

        # Top texture (per category)
        top_png = generate_top_texture(category)
        with open(tex_dir / f"{name}_top.png", 'wb') as f:
            f.write(top_png)

        # Bottom texture (per category)
        bottom_png = generate_bottom_texture(category)
        with open(tex_dir / f"{name}_bottom.png", 'wb') as f:
            f.write(bottom_png)

        # Blockstate
        write_json(blockstate_dir / f"{name}.json", generate_blockstate(name))

        # Block model
        write_json(block_model_dir / f"{name}.json", generate_block_model(name))

        # Item model (legacy)
        write_json(item_model_dir / f"{name}.json", generate_item_model(name))

        # Item definition (1.21.4+)
        write_json(item_def_dir / f"{name}.json", generate_item_definition(name))

        # Loot table
        write_json(loot_dir / f"{name}.json", generate_loot_table(name))

    # Process DNU bombs
    for name, display_name, yield_kt, country in DNU_BOMBS:
        print(f"  Generating DNU: {name}")

        # Side texture with hazard stripes
        flag_country = country
        side_png = generate_block_texture(name, "dnu", flag_country)
        with open(tex_dir / f"{name}_side.png", 'wb') as f:
            f.write(side_png)

        top_png = generate_top_texture("dnu")
        with open(tex_dir / f"{name}_top.png", 'wb') as f:
            f.write(top_png)

        bottom_png = generate_bottom_texture("dnu")
        with open(tex_dir / f"{name}_bottom.png", 'wb') as f:
            f.write(bottom_png)

        write_json(blockstate_dir / f"{name}.json", generate_blockstate(name))
        write_json(block_model_dir / f"{name}.json", generate_block_model(name))
        write_json(item_model_dir / f"{name}.json", generate_item_model(name))
        write_json(item_def_dir / f"{name}.json", generate_item_definition(name))
        write_json(loot_dir / f"{name}.json", generate_loot_table(name))

    # Language file
    write_json(lang_dir / "en_us.json", generate_lang())

    # Crafting recipes
    recipes = generate_crafting_recipes()
    for name, recipe in recipes.items():
        write_json(recipe_dir / f"{name}.json", recipe)

    # Count generated files
    all_names = [b[0] for b in BOMBS] + [b[0] for b in DNU_BOMBS]
    total = len(all_names)
    print(f"\nGenerated resources for {total} bombs:")
    print(f"  - {total * 3} textures (side, top, bottom)")
    print(f"  - {total} blockstates")
    print(f"  - {total} block models")
    print(f"  - {total} item models (legacy)")
    print(f"  - {total} item definitions (1.21.4)")
    print(f"  - {total} loot tables")
    print(f"  - {len(recipes)} crafting recipes")
    print(f"  - 1 language file")
    print("Done!")


if __name__ == "__main__":
    main()
