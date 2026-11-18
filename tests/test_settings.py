"""Run with python3 tests/test_settings.py (JDK 21 required)."""
from pathlib import Path
import subprocess
import tempfile

root = Path(__file__).resolve().parents[1]
with tempfile.TemporaryDirectory() as directory:
    tmp = Path(directory)
    loader = tmp / 'net/fabricmc/loader/api/FabricLoader.java'
    loader.parent.mkdir(parents=True)
    loader.write_text('''package net.fabricmc.loader.api;
public class FabricLoader {
    public static FabricLoader getInstance() { return new FabricLoader(); }
    public java.nio.file.Path getConfigDir() { return java.nio.file.Path.of(System.getProperty("test.config")); }
}''')
    test = tmp / 'SettingsTest.java'
    test.write_text('''package manor;
public class SettingsTest {
    static void check(boolean ok) { if (!ok) throw new AssertionError(); }
    public static void main(String[] args) throws Exception {
        if (args[0].equals("reload")) {
            check(Settings.chance("basher") == 3 && Settings.guaranteed("basher"));
            return;
        }
        check(Settings.guaranteed("necromancer"));
        Settings.toggle("necromancer");
        Settings.setChance("vindicator", 7);
        Settings.reset();
        check(Settings.guaranteed("necromancer"));
        check(Settings.chance("vindicator") == 50);
        check(Settings.chance("basher") == 100 && !Settings.guaranteed("basher"));
        Settings.setChance("vindicator", 100);
        check(Settings.select("basher2").equals("minecraft:illager_mansion/vindicator2"));
        Settings.setChance("evoker", 100);
        check(Settings.select("archivist").equals("minecraft:illager_mansion/evoker"));
        Settings.setChance("vindicator", 0);
        Settings.setChance("basher", 0);
        check(Settings.select("basher").equals("manor_compat:empty/basher"));
        Settings.setChance("basher", 3);
        Settings.setChance("marauder", 1);
        int bashers = 0;
        for (int i = 0; i < 10000; i++) {
            String chosen = Settings.select("basher2");
            if (chosen.equals("manor_compat:configured/basher2")) bashers++;
            else check(chosen.equals("manor_compat:configured/marauder2"));
        }
        check(bashers > 7100 && bashers < 7900);
        Settings.toggle("basher");
    }
}''')
    subprocess.run(['javac', '--release', '21', '-d', str(tmp), str(loader),
                    str(root / 'src/manor/Settings.java'), str(test)], check=True)
    for version in [None, '', '1', '4', 'invalid']:
        config = tmp / ('config-' + str(version))
        config.mkdir()
        file = config / 'manor-illagers.properties'
        if version is not None:
            file.write_text(f'configVersion={version}\nbasher=7\nbasher.guaranteed=true\n')
        command = ['java', f'-Dtest.config={config}', '-cp', str(tmp), 'manor.SettingsTest']
        subprocess.run(command + ['defaults'], check=True)
        subprocess.run(command + ['reload'], check=True)
        contents = file.read_text()
        assert '# DO NOT TOUCH\nconfigVersion=3' in contents
        assert contents.index('# Melee mobs') < contents.index('basher=') < contents.index('# Caster mobs')
print('Settings tests passed.')
