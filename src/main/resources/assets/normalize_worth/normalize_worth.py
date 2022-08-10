"""
items.json - from essentials plugin folder
worth.yml - from essentials plugin folder, but better from https://gist.github.com/queengooborg/92d08120f0d6d25175f6c7a30e3ccac7
or from https://gist.github.com/SvineruS/69fff6c3cf42b96609f6a1a2f90446d8 - fixed some bugs, but probably will not be updated
"""

import json
import yaml


def main():
    items = json.load(open('items.json'))  # remove comments from file if error
    worth = yaml.safe_load(open('worth.yml'))['worth']

    materials_worth = {get_item_material(k, items): v for k, v in worth.items()}
    print(materials_worth)
    open('../materials_worth.yml', 'w').write(yaml.safe_dump(materials_worth))


def get_item_material(item_name, items):
    if '{Potion' in item_name:
        item_name = parse_potion_name(item_name)

    if item_name not in items:
        print('Item not found:', item_name)
        return item_name
    item = items[item_name]
    if isinstance(item, dict):
        return item['material']

    return get_item_material(item, items)


def parse_potion_name(item_name):
    result = "{splash}{potion_name}{long}{strong}pot"
    potion_name = item_name.removeprefix('splash').removeprefix('potion{Potion:').removesuffix('}')
    potion_name = potion_name.removeprefix('long').removeprefix('strong')

    return result.format(splash='splash' if 'splash' in item_name else '',
                         potion_name=potion_name,
                         long='long' if 'long' in item_name else '',
                         strong='strong' if 'strong' in item_name else ''
                         )


if __name__ == '__main__':
    main()
