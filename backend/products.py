"""Серверный каталог товаров — единственный источник цен.

Цены с фронта не принимаются: бэк всегда пересчитывает сумму по этому словарю.
"""

PRODUCTS = {
    "unban":           {"name": "Разбан",          "price": 599.0,  "category": "shop"},
    "unmute":          {"name": "Размут",          "price": 199.0,  "category": "shop"},
    "unwarn":          {"name": "Разварн",         "price": 49.0,   "category": "shop"},
    "access_seasonal": {"name": "Сезонная проходка", "price": 259.0,  "category": "access"},
    "ichoplus_1m":     {"name": "IchoPlus 1 мес",  "price": 199.0,  "category": "subscription"},
    "ichoplus_2m":     {"name": "IchoPlus 2 мес",  "price": 349.0,  "category": "subscription"},
    "ichoplus_3m":     {"name": "IchoPlus 3 мес",  "price": 449.0,  "category": "subscription"},
}


def get_product(sku: str):
    return PRODUCTS.get(sku)
