"""Usage: python3 build.py (requires Java 21)."""
from pathlib import Path
import json, subprocess, tempfile, zipfile
root = Path(__file__).resolve().parent
version = json.loads((root / 'resources/fabric.mod.json').read_text())['version']
output = root / 'dist' / f'Manor-Illager-Compat-{version}.jar'
with tempfile.TemporaryDirectory() as tmp:
    sources = [str(file) for folder in ('src', 'compile-api') for file in sorted((root / folder).rglob('*.java'))]
    subprocess.run(['java','-m','jdk.compiler/com.sun.tools.javac.Main','--release','21','-proc:none','-cp',tmp,'-d',tmp,*sources],check=True)
    # Mixin reads @Mixin from invisible annotations, but injectors from visible ones.
    bytecode = subprocess.run(
        ['javap', '-v', '-p', '-classpath', tmp, 'manor.mixin.TemplateMixin'],
        check=True, capture_output=True, text=True).stdout
    import re
    for attribute, annotation in [
        ('RuntimeInvisibleAnnotations', 'org.spongepowered.asm.mixin.Mixin'),
        ('RuntimeVisibleAnnotations', 'org.spongepowered.asm.mixin.injection.ModifyVariable'),
    ]:
        sections = re.findall(r'(?m)^\s*' + attribute + r':\n((?:[ \t]+[^\n]*\n)*)', bytecode)
        if not any(annotation + '(' in section for section in sections):
            raise RuntimeError(f'{annotation} missing from {attribute}; check compile-api retention')
    output.parent.mkdir(exist_ok=True)
    with zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as jar:
        for folder, files in [(root / 'resources',(root / 'resources').rglob('*')), (Path(tmp),(Path(tmp) / 'manor').rglob('*.class'))]:
            for file in sorted(files):
                if file.is_file() and file.name != ".DS_Store":
                    jar.write(file, file.relative_to(folder))

print(output)
