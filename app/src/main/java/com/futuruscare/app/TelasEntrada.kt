package com.futuruscare.app

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.util.Patterns
import android.widget.CheckBox
import android.widget.FrameLayout

fun MainActivity.abertura() {
    val p = pagina()
    val meio = altura / 2
    ui.imagem(p, R.drawable.logo_simbolo, 163, meio - 176, 72, 90)
    ui.imagem(p, R.drawable.logo, 13, meio - 36, 368, 51, "FuturusCare")
    ui.texto(p, "Cuidando de quem você mais ama", 20, meio + 48, 353, 38, 20, Cinza, centro = true)
    repeat(3) { ui.caixa(p, 176 + it * 16, meio + 176, 8, 8, if (it == 0) Roxo else 0xFFCBBBE5.toInt(), 8) }
    ui.texto(p, "v1.0.0", 150, meio + 193, 93, 30, 12, Cinza, centro = true)
}

fun MainActivity.introducao(valores: Boolean) {
    val h = altura.coerceAtLeast(790)
    val p = pagina(h)
    ui.imagem(p, R.drawable.logo, 16, 79, 368, 51, "FuturusCare")
    if (valores) {
        ui.texto(p, "Nossos valores", 20, 145, 353, 38, 30, Roxo, true)
        ui.texto(p, "Segurança\nCuidado\nAmor", 20, 187, 353, 89, 24, 0xFF542D8D.toInt())
        ui.imagem(p, R.drawable.intro_pets, 10, h - 512, 373, 300, "Gato ao lado de um cachorro")
    } else {
        ui.texto(p, "Cuidando de quem você mais ama", 32, 143, 329, 31, 18, Cinza, centro = true)
        ui.texto(p, "Bem-vindo(a)!", 20, 220, 353, 42, 30, 0xFF8053C2.toInt(), centro = true)
        ui.imagem(p, R.drawable.intro_dog, 10, h - 555, 373, 343, "Cachorro usando fones de ouvido")
    }
    ui.caixa(p, 159, h - 179, 31, 10, if (valores) 0xFFC3ACE8.toInt() else Roxo, 8)
    ui.caixa(p, 203, h - 179, 31, 10, if (valores) Roxo else 0xFFC3ACE8.toInt(), 8)
    ui.botao(p, "Get Started", 22, h - 142, 357, 75) { mostrar(if (valores) "escolha" else "valores") }.apply {
        setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 28 * ui.escala)
        typeface = Typeface.MONOSPACE
        background = ui.fundo(Roxo, 25)
    }
}

private fun MainActivity.tipoConta(p: FrameLayout, y: Int, compacto: Boolean, aoMudar: () -> Unit) {
    val x = if (compacto) 29 else 4
    val largura = if (compacto) 144 else 157
    val espaco = if (compacto) 164 else 160
    listOf(if (compacto) "Cliente" else "Sou Cliente", if (compacto) "PetSitter" else "Sou Cuidador").forEachIndexed { i, nome ->
        ui.botao(p, nome, x + i * espaco, y, largura, 36, (i == 1) != cuidador) {
            cuidador = i == 1
            aoMudar()
        }.apply {
            textSize = 14f * ui.escala / resources.displayMetrics.scaledDensity
            background = ui.fundo(if ((i == 1) == cuidador) Roxo else Color.TRANSPARENT, 11, Roxo)
        }
    }
}

fun MainActivity.acesso(cadastrar: Boolean) {
    val p = pagina(altura.coerceAtLeast(760))
    if (cadastrar) ui.imagem(p, R.drawable.cadastro_hero, 0, 0, 393, 400)
    else ui.imagem(p, R.drawable.login_hero, 161, 13, 232, 270)
    val topo = if (cadastrar) 227 else 283
    val cartao = ui.caixa(p, 13, topo, 366, 398, 0xFFFFFDEE.toInt(), 26)
    ui.texto(cartao, if (cadastrar) "Cadastre-se" else "Acesse sua Conta", 10, 21, 346, 40, 28, Color.BLACK, true, true)
    tipoConta(cartao, 82, true) { acessoReabrir(cadastrar) }
    ui.texto(cartao, "E-mail", 37, 145, 270, 29, 18, Color.BLACK, true)
    val email = ui.campo(cartao, "@ Seu e-mail", 29, 173, 308, 43, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    ui.texto(cartao, "Senha", 37, 227, 270, 29, 18, Color.BLACK, true)
    val senha = ui.campo(cartao, "Sua senha", 29, 255, 308, 43, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
    ui.botao(cartao, if (cadastrar) "Cadastrar" else "Entrar", 111, 324, 144, 36) {
        if (cadastrar) {
            mostrar("cadastro")
            findViewById<android.widget.EditText>(emailCadastroId)?.setText(email.text)
            findViewById<android.widget.EditText>(senhaCadastroId)?.setText(senha.text)
        } else if (email.text.isBlank() || senha.text.isBlank()) {
            aviso("Preencha o e-mail e a senha.")
        } else if (dados.entrar(email.text.toString(), senha.text.toString(), cuidador)) {
            mostrar("home")
        } else aviso("Confira o e-mail, a senha e o tipo de conta.")
    }.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 14 * ui.escala)
    ui.texto(cartao, if (cadastrar) "Entrar" else "Não é cliente? Cadastre-se", 30, 366, 306, 30, 12, Roxo, centro = true)
        .setOnClickListener { mostrar(if (cadastrar) "login" else "cadastro") }
    ui.texto(p, "Cuidar de quem você ama ficou fácil", 20, topo + 409, 353, 31, 14, Color.BLACK, true, true)
}

private fun MainActivity.acessoReabrir(cadastrar: Boolean) = mostrar(if (cadastrar) "escolha" else "login")

fun MainActivity.cadastro() {
    val p = pagina(if (cuidador) 1000 else 915)
    ui.imagem(p, R.drawable.logo, 127, 72, 140, 20, "FuturusCare")
    ui.texto(p, "CUIDANDO DE QUEM VOCÊ MAIS AMA", 40, 102, 313, 21, 12, Cinza, centro = true)
    ui.texto(p, if (cuidador) "Cadastro de Cuidador" else "Crie sua Conta", 20, 128, 353, 28, 23, Texto, true, true)
    val caixa = ui.caixa(p, 16, 166, 361, if (cuidador) 684 else 592, raio = 25)
    val abas = ui.caixa(caixa, 20, 16, 321, 45, raio = 16, borda = Linha)
    tipoConta(abas, 4, false) { mostrar("cadastro") }
    fun entrada(rotulo: String, dica: String, y: Int, tipo: Int = InputType.TYPE_CLASS_TEXT): android.widget.EditText {
        ui.texto(caixa, rotulo, 20, y, 321, 22, 14, Texto, true)
        val campo = ui.campo(caixa, dica, 20, y + 26, 321, 43, tipo)
        val icone = when {
            rotulo.contains("Senha") -> R.drawable.ic_cadeado
            rotulo.contains("mail") -> R.drawable.ic_email
            rotulo.contains("Telefone") -> R.drawable.ic_telefone
            rotulo.contains("Endereço") -> R.drawable.ic_local
            rotulo.contains("Experiência") -> R.drawable.ic_agenda
            else -> R.drawable.ic_usuario
        }
        campo.setPadding(ui.px(46), ui.px(6), ui.px(if (rotulo.contains("Senha")) 46 else 14), ui.px(6))
        ui.icone(caixa, icone, 34, y + 37, 21)
        if (rotulo.contains("Senha")) {
            val olho = ui.caixa(caixa, 297, y + 25, 44, 44, Color.TRANSPARENT, 0)
            ui.icone(olho, R.drawable.ic_olho, 11, 11, 22)
            olho.contentDescription = "Mostrar senha"
            olho.setOnClickListener {
                val visivel = campo.transformationMethod != null
                campo.transformationMethod = if (visivel) null else android.text.method.PasswordTransformationMethod.getInstance()
                campo.setSelection(campo.text.length)
                olho.contentDescription = if (visivel) "Ocultar senha" else "Mostrar senha"
            }
        }
        return campo
    }
    val nome = entrada("Nome Completo", if (cuidador) "ex: João Silva" else "ex: Maria Silva", 76)
    val email = entrada(if (cuidador) "E-mail Profissional" else "E-mail", "maria@exemplo.com", 151,
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS).apply { id = emailCadastroId }
    val telefone = entrada(if (cuidador) "Telefone de Contato" else "Telefone", "(11) 99999-9999", 226, InputType.TYPE_CLASS_PHONE)
    var especialidade: android.widget.Spinner? = null
    var experiencia: android.widget.EditText? = null
    if (cuidador) {
        ui.texto(caixa, "Especialidade Principal", 20, 301, 321, 22, 14, Texto, true)
        especialidade = ui.selecao(caixa, servicos, 20, 327, 321)
        experiencia = entrada("Experiência (Anos)", "ex: 3 anos de experiência", 389, InputType.TYPE_CLASS_NUMBER)
    }
    val senha = entrada(if (cuidador) "Senha de Acesso" else "Senha", "Mínimo 8 caracteres", if (cuidador) 464 else 301,
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD).apply { id = senhaCadastroId }
    val confirmar = if (!cuidador) entrada("Confirmar Senha", "Repita sua senha", 376,
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) else null
    val endereco = entrada("Endereço", "Digite seu endereço", if (cuidador) 545 else 459)
    val aceitar = CheckBox(this).apply { buttonTintList = android.content.res.ColorStateList.valueOf(Roxo) }
    ui.colocar(caixa, aceitar, 14, if (cuidador) 630 else 542, 42, 46)
    ui.texto(caixa, "Eu aceito os Termos de Serviço e a Política de Privacidade.", 52, if (cuidador) 633 else 541,
        285, 43, 13, Roxo).setOnClickListener {
        AlertDialog.Builder(this).setTitle("Sobre este aplicativo")
            .setMessage("Projeto escolar de demonstração. Os cadastros, pets, agendamentos e mensagens ficam somente neste aparelho. Não há contratação de serviços, cobrança ou envio de mensagens para outras pessoas. Ao limpar os dados ou desinstalar o app, as informações são apagadas.")
            .setPositiveButton("Entendi", null).show()
    }
    val fim = if (cuidador) 863 else 770
    ui.botao(p, if (cuidador) "Enviar Cadastro" else "Cadastrar", 36, fim, 321) {
        val mensagem = when {
            nome.text.isBlank() -> "Informe seu nome."
            !Patterns.EMAIL_ADDRESS.matcher(email.text).matches() -> "Informe um e-mail válido."
            telefone.text.count { it.isDigit() } < 10 -> "Informe o telefone com DDD."
            senha.text.length < 8 -> "A senha precisa ter pelo menos 8 caracteres."
            confirmar != null && senha.text.toString() != confirmar.text.toString() -> "As senhas precisam ser iguais."
            endereco.text.isBlank() -> "Informe o endereço."
            cuidador && experiencia?.text.isNullOrBlank() -> "Informe sua experiência."
            !aceitar.isChecked -> "Leia e aceite os termos para continuar."
            else -> ""
        }
        if (mensagem.isNotEmpty()) aviso(mensagem)
        else if (dados.cadastrar(nome.text.toString(), email.text.toString(), telefone.text.toString(), endereco.text.toString(),
                senha.text.toString(), cuidador, especialidade?.selectedItem?.toString() ?: "", experiencia?.text?.toString() ?: "")) {
            mostrar("home")
        } else aviso("Já existe uma conta com esse e-mail neste aparelho.")
    }
    ui.texto(p, "Já tem uma conta? Faça login", 36, fim + 59, 321, 40, 14, Roxo, centro = true).setOnClickListener { mostrar("login") }
}
