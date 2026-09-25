# VI Countdown

Aplicativo Android independente de contagem regressiva para o lançamento de Grand Theft Auto VI.

**Data padrão:** 19 de novembro de 2026.

## Recursos
- Contagem regressiva em tempo real, sem valores negativos.
- Estado especial de lançamento.
- Mensagem diária determinística.
- Marcos de 100, 90, 60, 30, 14, 7, 3, 2 e 1 dia.
- Central de mensagens.
- Notificação diária configurável e notificação de teste.
- Som/vibração, horário e reprogramação após reinicialização.
- Permissão de notificações no Android 13+.
- Navegação Início / Mensagens / Ajustes.
- Identidade visual original escura com neon rosa/ciano.
- Workflow GitHub Actions `Gerar APK`.

## Aviso
Projeto independente, não oficial e feito por fã. Não é afiliado, endossado ou patrocinado pela Rockstar Games ou Take-Two Interactive. Não inclui logotipos, imagens, personagens, músicas, fontes proprietárias ou outros ativos oficiais.

## Build
Abra no Android Studio ou use Gradle 8.11.1 + JDK 17:

`gradle assembleDebug`

O APK será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

> Observação: este pacote inclui `gradlew`/`gradlew.bat` como launchers leves para ambientes que já tenham Gradle. O GitHub Actions instala a versão correta automaticamente.
