[README.md](https://github.com/user-attachments/files/35278115/README.md)

# 🐾 FuturusCare

Aplicativo Android para **tutores de pets encontrarem e agendarem cuidadores e serviços** (passeio, pet sitting, hospedagem, banho & tosa), com cadastro de pets, agenda e chat.

> Projeto acadêmico / protótipo funcional. Todos os dados são salvos **localmente no dispositivo** — não há backend.

---

## ✨ Funcionalidades

- **Onboarding**: tela de abertura, introdução e apresentação dos valores do app
- **Autenticação local**: cadastro e login como **Tutor** ou **Cuidador** (senha protegida com PBKDF2-HMAC-SHA256)
- **Home**: busca de cuidadores/serviços, atalhos por tipo de serviço e próximos compromissos
- **Meus Pets**: cadastro, edição e foto do pet (espécie, raça, idade, peso, observações)
- **Agenda**: calendário semanal, novo agendamento (profissional, pet, serviço, data e hora) e filtro por status
- **Chat**: lista de conversas e troca de mensagens persistidas localmente
- **Perfil**: visualização/edição dos dados, perfil profissional do cuidador e logout

## 🧰 Tecnologias

| Item | Versão |
|---|---|
| Linguagem | Kotlin 2.1.20 |
| Android Gradle Plugin | 8.11.0 |
| compileSdk / targetSdk | 35 |
| minSdk | 27 (Android 8.1) |
| Java / JVM target | 17 |
| UI | Views nativas construídas por código (sem XML de layout e sem Compose) |
| Persistência | `SharedPreferences` + JSON (`org.json`) |
| Fonte | Quicksand |

O projeto **não usa bibliotecas externas** além do SDK do Android.

## 🚀 Como executar

### Pré-requisitos
- [Android Studio](https://developer.android.com/studio) (versão recente, compatível com AGP 8.11)
- JDK 17 (já incluso no Android Studio)
- Emulador ou celular com Android 8.1+ (API 27+)

### Passo a passo
```bash
git clone https://github.com/davidpedreira23/FuturusCareApp.git
cd FuturusCareApp
```
1. Abra a pasta no Android Studio (**File > Open**)
2. Aguarde o *Gradle Sync* terminar
3. Selecione um emulador/dispositivo e clique em **Run ▶**

Ou pela linha de comando:
```bash
./gradlew assembleDebug        # Linux/macOS (Windows: gradlew.bat)
./gradlew installDebug         # instala no dispositivo conectado
```
O APK é gerado em `app/build/outputs/apk/debug/`.

### 📦 APK pronto para instalar
O APK de teste (`FuturusCare.apk`) é entregue junto com a atividade. Para instalar no celular Android, abra o arquivo e permita a instalação de fontes desconhecidas, se o sistema pedir.

### 🔑 Conta de demonstração
Já existe uma conta de tutor pré-cadastrada no primeiro uso:

| E-mail | Senha |
|---|---|
| `mariana.silva@exemplo.com` | `12345678` |

> ⚠️ Credencial **apenas para demonstração**. Não use senhas reais no protótipo.

## 🗂️ Estrutura do projeto

```
FuturusCare/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/futuruscare/app/
│       │   │   ├── MainActivity.kt              # Activity única, navegação entre telas
│       │   │   ├── Componentes.kt               # Helpers de UI (caixa, texto, botão, campo...)
│       │   │   ├── DadosLocais.kt               # Modelos e persistência local
│       │   │   ├── TelasEntrada.kt              # Abertura, introdução, login, cadastro
│       │   │   ├── TelasPrincipais.kt           # Home, Meus Pets, Chat
│       │   │   ├── TelasCadastroPetAgenda.kt    # Formulário de pet e Agenda
│       │   │   └── TelasPerfil.kt               # Perfil, edição, perfil profissional
│       │   └── res/                             # Imagens, ícones, fonte e estilos
│       └── androidTest/.../VerificacaoApp.kt    # Teste instrumentado de verificação das telas
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/wrapper/
```

## 🏗️ Arquitetura

O app usa uma **Activity única** (`MainActivity`). A navegação é feita pela função `mostrar(tela: String)`, que limpa a raiz e desenha a tela pedida. Cada tela é uma *extension function* de `MainActivity`, organizada por arquivo.

| Rota | Tela |
|---|---|
| `abertura`, `intro`, `valores` | Splash e onboarding |
| `escolha`, `login`, `cadastro` | Acesso |
| `home`, `pets`, `agenda`, `chat`, `perfil` | Abas da barra inferior |
| `pet_form`, `conversa`, `editar_perfil`, `profissional` | Telas secundárias |

O botão "voltar" é tratado em `onBackPressed()`, e o estado básico (tela, perfil de cuidador, contato) sobrevive à rotação via `onSaveInstanceState`.

### Modelos de dados (`DadosLocais.kt`)

```kotlin
data class Pet(id, nome, especie, raca, idade, peso, observacoes, foto)
data class Compromisso(id, profissional, pet, servico, data, horario, confirmado)
```

### Persistência
`DadosLocais` grava tudo em `SharedPreferences` (arquivo `futuruscare`) como JSON: contas, sessão, pets, compromissos e mensagens por contato. Fotos de pets são salvas em `filesDir` (JPEG, redimensionadas para no máximo ~1200 px). Senhas são guardadas com **hash PBKDF2-HMAC-SHA256** com salt aleatório.

## 🧪 Testes

O projeto inclui um teste instrumentado (`VerificacaoApp`) que navega pelas telas do app. Com emulador ou dispositivo conectado:

```bash
./gradlew connectedAndroidTest
```

## ⚠️ Limitações conhecidas

- Sem backend: os dados ficam só no aparelho e não são sincronizados
- Cuidadores, clínica e conversas exibidos são **dados de exemplo** fixos
- Chat é local (não há troca de mensagens entre usuários reais)
- Interface construída por código com coordenadas fixas escaladas; sem layouts XML nem suporte a modo escuro

## 🗺️ Próximos passos

- [ ] Backend/API (autenticação e agendamentos reais)
- [ ] Chat em tempo real
- [ ] Notificações de agendamento
- [ ] Migrar a UI para Jetpack Compose
- [ ] Testes unitários para `DadosLocais`

## 👥 Autores

- David Derik
- Isaac
- Gustavo Lacerda
- Erick

## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais.
