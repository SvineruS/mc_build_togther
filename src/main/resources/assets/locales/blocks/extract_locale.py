import json
import sys
import zipfile
from pathlib import Path
from pprint import pprint

mc_path = Path.home() / '.minecraft'


def main(version=None, locale=None, save=False):
    versions = get_versions()
    if not versions:
        raise Exception("No versions found. Install ")

    if version is not None:
        if version not in versions:
            raise ValueError(f"Version {version} not found, available versions: {versions.keys()}")
    else:
        version = choose_from(versions, default='1.19')

    locales = get_locales(versions[version])
    locales['en_us'] = None  # en_us is always included, but stored in jar

    if locale is not None:
        if locale not in locales:
            raise ValueError(f"Locale {locale} not found, available locales: {locales.keys()}")
    else:
        locale = choose_from(locales, default='en_us')

    lang_file = extract_from_jar(version) if locale == 'en_us' else \
        extract_from_objects(locales[locale])

    blocks = get_blocks(lang_file)

    print(f"{locale=} {version=}")
    pprint(blocks)

    if save:
        save_path = Path.cwd() / (locale + ".yaml")
        yaml = f"# Autogenerated by svinerus.buildTogether extract_locale.py script \n" + f"# {locale=} {version=} \n" + \
               "\n".join((f"{key}: {name}" for key, name in blocks.items()))
        save_path.write_text(yaml)
        print(f"Saved to {save_path}")


def extract_from_objects(lang_hash):
    lang_path = mc_path / 'assets' / 'objects' / lang_hash[:2] / lang_hash
    return json.load(lang_path.open())


def extract_from_jar(version):
    with zipfile.ZipFile(mc_path / 'versions' / version / (version + '.jar')) as z:
        return json.load(z.open('assets/minecraft/lang/en_us.json'))


def get_versions():
    return {version.stem: version
            for version in (mc_path / 'assets' / 'indexes').iterdir()
            if version.is_file()}


def get_locales(index_file_path):
    index_file = json.load(index_file_path.open())
    return {path.removeprefix('minecraft/lang/').removesuffix('.json'): value['hash']
            for path, value in index_file['objects'].items()
            if path.startswith('minecraft/lang/')}


def get_blocks(lang_file):
    return {key.removeprefix('block.minecraft.'): block
            for key, block in lang_file.items()
            if key.startswith('block.minecraft.') and key.count('.') == 2}


def choose_from(li, default=None):
    while True:
        print("Choose from:\n", '\t'.join(li), sep='')
        i = input(f"-> [{default}]: ")
        if not i and default in li:
            return default
        if i in li:
            return i


if __name__ == '__main__':
    args = sys.argv[1:] + [None, None]
    main(version=args[0], locale=args[1], save=True)
    # main(version='1.19', locale='en_us', save=True)
    # main(version='1.19', locale='uk_ua', save=True)
