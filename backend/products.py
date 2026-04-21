"""Серверный каталог товаров — единственный источник цен.

Цены с фронта не принимаются: бэк всегда пересчитывает сумму по этому словарю.
"""

PRODUCTS = {
    "unban":           {"name": "Разбан",            "price": 1000.0, "category": "shop"},
    "unmute":          {"name": "Размут",            "price": 300.0,  "category": "shop"},
    "unwarn":          {"name": "Разварн",           "price": 50.0,   "category": "shop"},
    "access_monthly":  {"name": "Месячная проходка", "price": 249.0,  "category": "access"},
    "access_quarter":  {"name": "Проходка 3 мес",    "price": 499.0,  "category": "access"},
    "access_seasonal": {"name": "Сезонная проходка", "price": 699.0,  "category": "access"},
}


def get_product(sku: str):
    return PRODUCTS.get(sku)
