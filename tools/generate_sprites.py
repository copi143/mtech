#!/usr/bin/env python3
"""Generate placeholder sprite PNGs for the mtech mod."""

from PIL import Image, ImageDraw
import os

SPRITES_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "assets", "sprites")
TILE = 32

COLORS = {
    "tungsten-carbide":     (130, 150, 165),
    "reinforced-alloy":     (200, 130,  60),
    "composite-armor":      ( 80, 105,  45),
    "energy-crystal":       (120, 250, 210),
    "carbide-furnace":      ( 95, 115, 135),
    "alloy-crucible":       (175, 115,  55),
    "armor-compressor":     ( 75,  95,  45),
    "crystal-synthesizer":  ( 95, 190, 170),
    "carbide-furnace-top":      (140, 175, 215),
    "alloy-crucible-top":       (250, 195,  95),
    "armor-compressor-top":     (115, 155,  75),
    "crystal-synthesizer-top":  (175, 250, 235),
    "carbide-wall":             (120, 135, 148),
    "carbide-wall-large":       (110, 125, 138),
    "reinforced-wall":          (185, 115,  50),
    "reinforced-wall-large":    (175, 105,  40),
    "composite-wall":           ( 75,  97,  37),
    "composite-wall-large":     ( 65,  87,  27),
    "piercer":              (125, 140, 155),
    "thunder":              (110, 195, 175),
    "volcano":              (195,  95,  45),
    "annihilator":          ( 85, 105,  55),
    "piercer-heat":         (190, 215, 255),
    "thunder-heat":         (175, 250, 235),
    "volcano-heat":         (250, 175,  75),
    "annihilator-heat":     (195, 215, 145),
    "piercer-rotation":     (135, 150, 165),
    "thunder-rotation":     (120, 205, 185),
    "volcano-rotation":     (205, 105,  55),
    "annihilator-rotation": ( 95, 115,  65),
    "carbide-bullet":       (130, 150, 165),
    "energy-bullet":        (120, 250, 210),
    "missile-bullet":       (200, 130,  60),
    "plasma-bullet":        (250,  95,  45),
    "mtech-laser":          (190, 215, 255),
    "mtech-laser-end":      (255, 255, 255),
}


def lerp(a, b, t):
    return int(a + (b - a) * t)


def brighter(color, amt=30):
    return (min(color[0] + amt, 255), min(color[1] + amt, 255), min(color[2] + amt, 255))


def darker(color, amt=30):
    return (max(color[0] - amt, 0), max(color[1] - amt, 0), max(color[2] - amt, 0))


def draw_item(name, color):
    """Draw recognizable item shapes: ingot, plate, armor, crystal."""
    img = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = TILE // 2, TILE // 2

    if name == "tungsten-carbide":
        # Ingot shape: trapezoid body
        pts = [(12, 6), (20, 6), (26, 16), (22, 26), (10, 26), (6, 16)]
        draw.polygon(pts, fill=(*color, 255))
        draw.polygon(pts, outline=darker(color, 40), width=1)
        # bevel highlight
        draw.polygon([(12, 6), (20, 6), (18, 12), (14, 12)], fill=brighter(color, 50), width=0)

    elif name == "reinforced-alloy":
        # Plate with rivets
        draw.rounded_rectangle([4, 6, 28, 26], radius=3, fill=(*color, 255))
        draw.rounded_rectangle([4, 6, 28, 26], outline=darker(color, 40), radius=3, width=1)
        # rivets at corners
        for rx, ry in [(8, 10), (24, 10), (8, 22), (24, 22)]:
            draw.ellipse([rx - 2, ry - 2, rx + 2, ry + 2], fill=brighter(color, 60))
        # center line
        draw.line([(16, 8), (16, 24)], fill=darker(color, 30), width=1)

    elif name == "composite-armor":
        # Layered chevron / armor plate
        layers = [6, 10, 14, 18, 22]
        for i, ly in enumerate(layers):
            shade = lerp(color[0], brighter(color, 60)[0], i / len(layers))
            shade = (shade, lerp(color[1], brighter(color, 60)[1], i / len(layers)),
                     lerp(color[2], brighter(color, 60)[2], i / len(layers)))
            draw.polygon([(4, ly), (28, ly), (24, ly + 3), (8, ly + 3)], fill=(*shade, 255))

    elif name == "energy-crystal":
        # Faceted crystal diamond
        pts = [(16, 2), (28, 16), (16, 30), (4, 16)]
        draw.polygon(pts, fill=(*color, 255))
        draw.polygon(pts, outline=brighter(color, 60), width=1)
        # inner facets
        draw.polygon([(16, 2), (22, 16), (16, 18)], fill=brighter(color, 80))
        draw.polygon([(16, 18), (22, 16), (16, 30), (10, 16)], fill=darker(color, 20))
        # glow dot
        draw.ellipse([14, 12, 18, 16], fill=(255, 255, 255, 200))

    return img


def draw_crafter(name, color, size):
    """Draw recognizable crafter shapes."""
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    if name.startswith("carbide-furnace"):
        # Furnace: brick-lined box with fire opening
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # firebox
        fb_color = darker(color, 40)
        fw, fh = px // 2, px // 3
        fx, fy = (px - fw) // 2, px - fh - 6
        draw.rectangle([fx, fy, fx + fw, fy + fh], fill=(*fb_color, 255))
        # inner glow
        draw.rectangle([fx + 4, fy + 4, fx + fw - 4, fy + fh - 4], fill=brighter(darker(color, 20), 30))
        # bricks on top
        brick_h = 6
        for i in range(0, px, 8):
            bx = i
            bw = 8
            if i % 16 == 0:
                bx -= 4
            draw.rectangle([bx, 6, bx + bw, 6 + brick_h], fill=darker(color, 15), outline=darker(color, 40), width=1)
        # vent lines
        for vy in range(fy - 8, 5, -8):
            draw.line([(8, vy), (px - 8, vy)], fill=darker(color, 30), width=2)

    elif name.startswith("alloy-crucible"):
        # Crucible: pot with molten metal
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # crucible bowl
        bowl_top = px // 4
        bowl_bot = px - 6
        draw.polygon([
            (8, bowl_top), (px - 8, bowl_top), (px - 12, bowl_bot), (12, bowl_bot)
        ], fill=darker(color, 25))
        # molten metal surface
        draw.ellipse([10, bowl_top - 3, px - 10, bowl_top + 8], fill=brighter(color, 60))
        # glow
        draw.ellipse([14, bowl_top - 1, px - 14, bowl_top + 5], fill=(255, 230, 180, 200))
        # spout
        draw.polygon([(px - 8, bowl_top), (px - 2, bowl_top - 6), (px - 2, bowl_top)], fill=brighter(color, 20))

    elif name.startswith("armor-compressor"):
        # Press: hydraulic press with piston and base
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # base platform
        draw.rectangle([6, px - 12, px - 6, px - 4], fill=brighter(color, 20))
        draw.rectangle([6, px - 12, px - 6, px - 4], outline=darker(color, 30), width=1)
        # piston rod (vertical)
        draw.rectangle([px // 2 - 4, 10, px // 2 + 4, px // 2 + 4], fill=brighter(color, 30))
        # piston head
        draw.rectangle([px // 2 - 10, 6, px // 2 + 10, 16], fill=brighter(color, 50))
        draw.rectangle([px // 2 - 10, 6, px // 2 + 10, 16], outline=darker(color, 30), width=1)
        # press plate
        draw.rectangle([10, px // 2, px - 10, px // 2 + 6], fill=brighter(color, 40))
        # item on base
        draw.rectangle([px // 2 - 6, px - 18, px // 2 + 6, px - 12], fill=(*brighter(color, 70), 255))
        # guide rails
        draw.line([(12, 16), (12, px - 12)], fill=darker(color, 30), width=2)
        draw.line([(px - 12, 16), (px - 12, px - 12)], fill=darker(color, 30), width=2)

    elif name.startswith("crystal-synthesizer"):
        # Synthesizer: lab vat with crystals growing
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # vat chamber
        vat = [8, px // 3, px - 8, px - 6]
        draw.rectangle(vat, fill=darker(color, 20))
        draw.rectangle(vat, outline=brighter(color, 30), width=1)
        # liquid
        draw.ellipse([12, vat[3] - 12, px - 12, vat[3] + 2], fill=(*brighter(color, 40), 180))
        # crystals growing up
        crystal_pts = [
            (px // 2 - 10, vat[3] - 4, px // 2 - 10, vat[1] + 8),
            (px // 2, vat[3] - 2, px // 2, vat[1] + 2),
            (px // 2 + 10, vat[3] - 6, px // 2 + 10, vat[1] + 12),
        ]
        for cx1, cy1, cx2, cy2 in crystal_pts:
            draw.polygon([(cx1 - 4, cy1), (cx1 + 4, cy1), (cx2, cy2)], fill=(*brighter(color, 60), 255))
            draw.polygon([(cx1 - 4, cy1), (cx1 + 4, cy1), (cx2, cy2)], outline=(*brighter(color, 80), 255), width=1)
        # pipes on top
        for px2 in [10, px - 10]:
            draw.rectangle([px2 - 2, 4, px2 + 2, vat[1] - 2], fill=brighter(color, 20))
        # top rim
        draw.rectangle([6, vat[1] - 3, px - 6, vat[1]], fill=brighter(color, 30))

    return img


def draw_region_icon(name, color, size):
    """Draw a recognizable icon for a block type."""
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    if name.startswith("carbide-furnace"):
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # fire icon
        draw.ellipse([px // 2 - 6, px // 2 - 8, px // 2 + 6, px // 2 + 4], fill=(255, 255, 255, 200))
        draw.polygon([(px // 2, px // 2 - 12), (px // 2 - 4, px // 2 - 4), (px // 2 + 4, px // 2 - 4)], fill=(255, 255, 255, 200))
        draw.polygon([(px // 2 - 8, px // 2 + 2), (px // 2, px // 2 + 6), (px // 2 + 8, px // 2 + 2)], fill=(255, 255, 255, 180))
    elif name.startswith("alloy-crucible"):
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # droplet
        draw.polygon([(px // 2, 8), (px // 2 - 8, px // 2 + 4), (px // 2 + 8, px // 2 + 4)], fill=(255, 255, 255, 200))
        draw.ellipse([px // 2 - 8, px // 2 - 2, px // 2 + 8, px // 2 + 10], fill=(255, 255, 255, 200))
    elif name.startswith("armor-compressor"):
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # down arrow
        draw.polygon([(px // 2, px - 10), (px // 2 - 8, px // 2 + 2), (px // 2 + 8, px // 2 + 2)], fill=(255, 255, 255, 200))
        draw.rectangle([px // 2 - 3, 10, px // 2 + 3, px // 2 + 2], fill=(255, 255, 255, 200))
    elif name.startswith("crystal-synthesizer"):
        draw.rounded_rectangle([3, 3, px - 3, px - 3], radius=4, fill=(*color, 255))
        # sparkle
        draw.polygon([(px // 2, 8), (px // 2 - 4, px // 2), (px // 2, px - 8), (px // 2 + 4, px // 2)], fill=(255, 255, 255, 200))
        draw.polygon([(12, px // 2), (px // 2 - 4, px // 2 - 4), (px - 12, px // 2), (px // 2 + 4, px // 2 + 4)], fill=(255, 255, 255, 150))

    return img


def draw_block(name, color, size):
    """Draw a block: crafters get detailed shapes, others get generic look."""
    if "furnace" in name or "crucible" in name or "compressor" in name or "synthesizer" in name:
        return draw_crafter(name, color, size)
    # generic block
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.rounded_rectangle([2, 2, px - 2, px - 2], radius=4, fill=(*darker(color, 10), 255))
    draw.rounded_rectangle([2, 2, px - 2, px - 2], radius=4, outline=darker(color, 40), width=1)
    draw.rounded_rectangle([4, 4, px - 4, px - 4], radius=3, fill=(*color, 180))
    return img


def draw_turret(name, color, size):
    """Draw detailed turret base shapes."""
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = px // 2, px // 2

    # Turret base platform (common)
    r_base = px // 2 - 4
    draw.ellipse([cx - r_base, cy - 2, cx + r_base, cy + r_base + 2], fill=(*darker(color, 20), 255))
    draw.ellipse([cx - r_base, cy - 2, cx + r_base, cy + r_base + 2], outline=darker(color, 50), width=1)
    draw.ellipse([cx - r_base + 4, cy + 2, cx + r_base - 4, cy + r_base - 2], fill=(*color, 255))

    # Rotating mount circle
    r_mount = px // 4
    draw.ellipse([cx - r_mount, cy - r_mount + 2, cx + r_mount, cy + r_mount + 2], fill=(*brighter(color, 20), 255))

    if "piercer" in name:
        # Sniper turret: long barrel support + barrel mount
        bw = max(6, px // 10)
        # barrel support structure
        draw.rectangle([cx - bw * 2, cy - r_mount + 6, cx + bw * 2, cy + r_mount - 4], fill=(*darker(color, 15), 255))
        # barrel cradle
        draw.rectangle([cx - bw // 2, cy - r_mount - 4, cx + bw // 2, cy + r_mount + 2], fill=(*brighter(color, 15), 255))
        # barrel hint sticking up
        draw.rectangle([cx - bw // 4, cy - r_mount - 10, cx + bw // 4, cy - r_mount + 2], fill=(*brighter(color, 30), 255))
        # target reticle
        draw.line([(cx - 10, cy - 2), (cx - 4, cy - 2)], fill=(255, 255, 255, 180), width=1)
        draw.line([(cx + 4, cy - 2), (cx + 10, cy - 2)], fill=(255, 255, 255, 180), width=1)
        draw.line([(cx, cy - 8), (cx, cy - 2)], fill=(255, 255, 255, 180), width=1)

    elif "thunder" in name:
        # Tesla coil turret
        # central column
        col_w = max(6, px // 6)
        draw.rectangle([cx - col_w, cy - r_mount + 2, cx + col_w, cy + r_mount + 2], fill=(*brighter(color, 30), 255))
        # coil rings
        for i in range(3):
            ry = cy - r_mount + 6 + i * (px // 6)
            rw = col_w + 6 + i * 2
            draw.ellipse([cx - rw, ry - 2, cx + rw, ry + 2], fill=(*brighter(color, 50), 255))
            draw.ellipse([cx - rw, ry - 2, cx + rw, ry + 2], outline=(*brighter(color, 80), 255), width=1)
        # lightning bolts on sides
        for sign in [-1, 1]:
            lx = cx + sign * (col_w + 4)
            draw.line([(lx, cy - r_mount + 8), (lx + sign * 6, cy - 2), (lx, cy + 4), (lx + sign * 5, cy + r_mount - 4)],
                      fill=(255, 255, 255, 180), width=1)
        # top node
        draw.ellipse([cx - 4, cy - r_mount - 2, cx + 4, cy - r_mount + 6], fill=(255, 255, 255, 220))

    elif "volcano" in name:
        # Short wide cannon
        # wide barrel base
        bw = px // 3
        draw.rectangle([cx - bw, cy - r_mount + 2, cx + bw, cy + r_mount - 4], fill=(*color, 255))
        draw.rectangle([cx - bw, cy - r_mount + 2, cx + bw, cy + r_mount - 4], outline=darker(color, 40), width=1)
        # barrel mouth
        draw.rectangle([cx - bw + 4, cy - r_mount - 2, cx + bw - 4, cy - r_mount + 4], fill=(*brighter(color, 40), 255))
        # vent holes
        for vx in [cx - bw + 6, cx + bw - 6]:
            draw.ellipse([vx - 2, cy - 2, vx + 2, cy + 4], fill=(0, 0, 0, 100))
        # muzzle detail
        draw.rectangle([cx - bw + 2, cy - r_mount - 4, cx + bw - 2, cy - r_mount], fill=(*brighter(color, 50), 255))

    elif "annihilator" in name:
        # Massive double-barrel
        # twin barrels
        for sign in [-1, 1]:
            bx = cx + sign * (px // 8)
            bw = max(4, px // 10)
            draw.rounded_rectangle([bx - bw // 2, cy - r_mount - 4, bx + bw // 2, cy + r_mount + 2],
                                   radius=2, fill=(*brighter(color, 20), 255))
            draw.rounded_rectangle([bx - bw // 2, cy - r_mount - 4, bx + bw // 2, cy + r_mount + 2],
                                   radius=2, outline=darker(color, 40), width=1)
            # barrel tip
            draw.rounded_rectangle([bx - bw // 2 + 2, cy - r_mount - 8, bx + bw // 2 - 2, cy - r_mount - 2],
                                   radius=1, fill=(*brighter(color, 40), 255))
        # center mount
        draw.ellipse([cx - 8, cy - 6, cx + 8, cy + 8], fill=(*brighter(color, 10), 255))
        draw.ellipse([cx - 4, cy - 2, cx + 4, cy + 4], fill=(*brighter(color, 40), 255))

    return img


def draw_rotator(name, color, size):
    """Draw turret rotation (barrel) region that aligns with the base."""
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = px // 2, px // 2

    if "piercer" in name:
        # Long thin barrel pointing up
        bw = max(4, px // 8)
        bl = px // 2 + 4
        draw.rounded_rectangle([cx - bw // 2, cy - bl, cx + bw // 2, cy + 6], radius=2, fill=(*color, 255))
        draw.rounded_rectangle([cx - bw // 2, cy - bl, cx + bw // 2, cy + 6], radius=2, outline=darker(color, 40), width=1)
        # muzzle brake
        draw.rectangle([cx - bw // 2 - 2, cy - bl, cx + bw // 2 + 2, cy - bl + 4], fill=(*brighter(color, 30), 255))
        # breech
        draw.rectangle([cx - bw // 2 - 1, cy + 2, cx + bw // 2 + 1, cy + 6], fill=(*brighter(color, 15), 255))

    elif "thunder" in name:
        # Coil assembly
        col_w = max(6, px // 6)
        col_h = px // 2
        # central rod
        draw.rectangle([cx - 2, cy - col_h, cx + 2, cy + 6], fill=(*color, 255))
        # coil rings
        for i in range(4):
            ry = cy - col_h + 6 + i * (col_h // 5)
            rw = col_w + i * 2
            draw.ellipse([cx - rw, ry - 2, cx + rw, ry + 2], fill=(*brighter(color, 30), 255))
            draw.ellipse([cx - rw, ry - 2, cx + rw, ry + 2], outline=darker(color, 30), width=1)
        # top emitter
        draw.ellipse([cx - 5, cy - col_h - 2, cx + 5, cy - col_h + 6], fill=(*brighter(color, 60), 255))

    elif "volcano" in name:
        # Wide short barrel
        bw = px // 3
        bh = px // 3
        draw.rounded_rectangle([cx - bw, cy - bh, cx + bw, cy + 6], radius=4, fill=(*color, 255))
        draw.rounded_rectangle([cx - bw, cy - bh, cx + bw, cy + 6], radius=4, outline=darker(color, 40), width=1)
        # muzzle
        draw.rounded_rectangle([cx - bw + 4, cy - bh, cx + bw - 4, cy - bh + 6], radius=2, fill=(*brighter(color, 40), 255))
        # barrel rings
        for ry in [cy - bh + 12, cy - 6]:
            draw.line([(cx - bw + 2, ry), (cx + bw - 2, ry)], fill=darker(color, 30), width=2)

    elif "annihilator" in name:
        # Twin barrels
        for sign in [-1, 1]:
            bx = cx + sign * (px // 8)
            bw = max(4, px // 10)
            bl = px // 2 + 4
            draw.rounded_rectangle([bx - bw // 2, cy - bl, bx + bw // 2, cy + 6], radius=2, fill=(*color, 255))
            draw.rounded_rectangle([bx - bw // 2, cy - bl, bx + bw // 2, cy + 6], radius=2, outline=darker(color, 40), width=1)
            # barrel tip
            draw.rectangle([bx - bw // 2 - 1, cy - bl, bx + bw // 2 + 1, cy - bl + 4], fill=(*brighter(color, 30), 255))
        # connecting brace
        draw.rectangle([cx - px // 6, cy - 2, cx + px // 6, cy + 4], fill=(*color, 255))

    return img


def draw_effect(name, color, size):
    """Draw effect textures with recognizable shapes."""
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = px // 2, px // 2

    if "heat" in name or "top" in name:
        # Glow overlay: radial with pattern matching the block type
        if "piercer" in name:
            # Bright dot with crosshairs
            draw.ellipse([cx - 6, cy - 6, cx + 6, cy + 6], fill=(*color, 220))
            draw.line([(cx - 14, cy), (cx - 8, cy)], fill=(*color, 180), width=2)
            draw.line([(cx + 8, cy), (cx + 14, cy)], fill=(*color, 180), width=2)
            draw.line([(cx, cy - 14), (cx, cy - 8)], fill=(*color, 180), width=2)
            draw.line([(cx, cy + 8), (cx, cy + 14)], fill=(*color, 180), width=2)
        elif "thunder" in name:
            # Lightning glow
            draw.ellipse([cx - px // 4, cy - px // 4, cx + px // 4, cy + px // 4], fill=(*color, 100))
            for _ in range(3):
                lx = cx + (_ - 1) * 10
                ly_parts = [(cy - 12), (cy - 4), (cy + 4), (cy + 12)]
                pts = [(lx, y) for y in ly_parts]
                draw.line(pts, fill=(*color, 200), width=2)
        elif "volcano" in name:
            # Fire glow
            draw.ellipse([cx - px // 3, cy - px // 3, cx + px // 3, cy + px // 3], fill=(*color, 80))
            draw.polygon([(cx, cy - 16), (cx - 10, cy + 4), (cx + 10, cy + 4)], fill=(*color, 200))
            draw.polygon([(cx - 8, cy), (cx, cy - 10), (cx + 8, cy)], fill=(255, 255, 255, 180))
        elif "annihilator" in name:
            # Toxic/energy glow
            draw.ellipse([cx - px // 3, cy - px // 3, cx + px // 3, cy + px // 3], fill=(*color, 80))
            for r in range(3):
                rr = 8 + r * 6
                draw.ellipse([cx - rr, cy - rr + 2, cx + rr, cy + rr + 2], outline=(*color, 180 - r * 30), width=1)
        else:
            # Generic glow
            draw.ellipse([cx - px // 4, cy - px // 4, cx + px // 4, cy + px // 4], fill=(*color, 120))
            draw.ellipse([cx - 4, cy - 4, cx + 4, cy + 4], fill=(*brighter(color, 80), 220))

    elif "laser" in name:
        if "end" in name:
            draw.ellipse([cx - 4, cy - 4, cx + 4, cy + 4], fill=(*color, 255))
            draw.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=(*brighter(color, 40), 255))
        else:
            # Horizontal beam
            draw.rectangle([0, cy - 2, px, cy + 2], fill=(*color, 255))
            draw.rectangle([0, cy - 1, px, cy + 1], fill=(*brighter(color, 60), 255))

    return img


def draw_wall(name, color, large=False):
    """Draw walls with brick patterns."""
    size = 2 if large else 1
    px = size * TILE
    img = Image.new("RGBA", (px, px), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Brick background
    draw.rectangle([0, 0, px - 1, px - 1], fill=(*color, 255))

    mortar = darker(color, 50)
    if large:
        # 4x4 brick pattern
        brick_w = px // 4
        brick_h = px // 4
        for row in range(4):
            for col in range(4):
                bx = col * brick_w
                by = row * brick_h
                offset = brick_w // 2 if row % 2 == 0 else 0
                draw.rectangle([bx + offset + 1, by + 1, bx + brick_w + offset - 2, by + brick_h - 2],
                               fill=(*brighter(color, 15 if (row + col) % 2 == 0 else -5), 255))
                draw.rectangle([bx + offset + 1, by + 1, bx + brick_w + offset - 2, by + brick_h - 2],
                               outline=mortar, width=1)
    else:
        # 2x2 brick pattern
        brick_w = px // 2
        brick_h = px // 2
        for row in range(2):
            for col in range(2):
                bx = col * brick_w
                by = row * brick_h
                offset = brick_w // 2 if row % 2 == 0 else 0
                draw.rectangle([bx + offset + 1, by + 1, bx + brick_w + offset - 2, by + brick_h - 2],
                               fill=(*brighter(color, 20 if (row + col) % 2 == 0 else -10), 255))
                draw.rectangle([bx + offset + 1, by + 1, bx + brick_w + offset - 2, by + brick_h - 2],
                               outline=mortar, width=1)

    # surface highlight (top edge)
    draw.line([(0, 0), (px - 1, 0)], fill=brighter(color, 40), width=1)
    # shadow (bottom + right edge)
    draw.line([(0, px - 1), (px - 1, px - 1)], fill=mortar, width=1)
    draw.line([(px - 1, 0), (px - 1, px - 1)], fill=mortar, width=1)

    return img


def draw_bullet(name, color):
    """Draw recognizable bullet shapes."""
    img = Image.new("RGBA", (TILE // 2, TILE // 2), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    w, h = TILE // 2, TILE // 2
    cx, cy = w // 2, h // 2

    if "carbide" in name:
        # Pointed dart / AP round
        pts = [(2, h - 2), (w // 2, 1), (w - 2, h - 2)]
        draw.polygon(pts, fill=(*color, 255))
        draw.polygon(pts, outline=darker(color, 40), width=1)

    elif "energy" in name:
        # Glowing orb
        draw.ellipse([2, 2, w - 3, h - 3], fill=(*color, 255))
        draw.ellipse([4, 4, w - 5, h - 5], fill=(*brighter(color, 60), 255))
        draw.ellipse([w // 2 - 2, h // 2 - 2, w // 2 + 2, h // 2 + 2], fill=(255, 255, 255, 200))

    elif "missile" in name:
        # Rocket shape
        draw.rectangle([3, 0, w - 4, h - 2], fill=(*color, 255))
        draw.polygon([(2, 0), (w - 3, 0), (w // 2, -4)], fill=(*color, 255))  # nose
        # fins
        draw.polygon([(2, h - 4), (2, h - 1), (-1, h - 1)], fill=(*darker(color, 20), 255))
        draw.polygon([(w - 3, h - 4), (w - 3, h - 1), (w, h - 1)], fill=(*darker(color, 20), 255))

    elif "plasma" in name:
        # Fireball / plasma blob
        draw.ellipse([1, 1, w - 2, h - 2], fill=(*color, 255))
        draw.ellipse([3, 3, w - 4, h - 4], fill=(*brighter(color, 40), 200))
        # irregular highlights
        draw.ellipse([w // 2 - 2, 2, w // 2 + 2, 6], fill=(255, 255, 200, 180))
        draw.ellipse([2, h // 2 - 1, 5, h // 2 + 2], fill=(255, 255, 200, 150))

    return img


def main():
    os.makedirs(SPRITES_DIR, exist_ok=True)

    for name, color in COLORS.items():
        if "bullet" in name:
            img = draw_bullet(name, color)
        elif "rotation" in name:
            size = 4 if "annihilator" in name else (3 if "volcano" in name else 2)
            img = draw_rotator(name, color, size)
        elif "wall-large" in name:
            img = draw_wall(name, color, large=True)
        elif "wall" in name:
            img = draw_wall(name, color, large=False)
        elif "heat" in name or "top" in name or "laser" in name:
            sz = 4 if "annihilator" in name else (3 if "volcano" in name or "armor-compressor" in name else 2)
            img = draw_effect(name, color, size=sz)
        elif name in ("tungsten-carbide", "reinforced-alloy", "composite-armor", "energy-crystal"):
            img = draw_item(name, color)
        else:
            size = 4 if "annihilator" in name else (3 if "volcano" in name or "armor-compressor" in name else 2)
            img = draw_turret(name, color, size) if any(t in name for t in ["piercer", "thunder", "volcano", "annihilator"]) else draw_block(name, color, size)
        img.save(os.path.join(SPRITES_DIR, f"{name}.png"))

    print(f"Generated {len(COLORS)} placeholder sprites in {SPRITES_DIR}")


if __name__ == "__main__":
    main()
