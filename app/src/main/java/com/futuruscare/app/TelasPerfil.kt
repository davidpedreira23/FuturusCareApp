package com.futuruscare.app

import android.app.AlertDialog
import android.graphics.Color
import android.text.InputType

fun MainActivity.perfil() {
    val usuario = dados.perfil()
    val p = pagina(750, 4)
    titulo(p, "Meu Perfil")
    ui.imagem(p, R.drawable.perfil_banner, 24, 79, 349, 111, "Capa do perfil com pets")
    ui.texto(p, usuario.optString("nome"), 30, 200, 333, 36, 22, Texto, true, true)
        .setOnClickListener { mostrar("editar_perfil") }
    val cartao = ui.caixa(p, 20, 255, 353, 229, raio = 17)
    ui.texto(cartao, "Informações Pessoais", 16, 13, 321, 25, 16, Roxo, true)
    ui.texto(cartao, "E-mail", 16, 45, 321, 19, 12, Cinza)
    ui.texto(cartao, usuario.optString("email"), 16, 65, 321, 27, 14, Texto, true)
    ui.texto(cartao, "Telefone", 16, 97, 321, 19, 12, Cinza)
    ui.texto(cartao, usuario.optString("telefone"), 16, 117, 321, 26, 14, Texto, true)
    ui.texto(cartao, "Endereço", 16, 149, 321, 19, 12, Cinza)
    ui.texto(cartao, usuario.optString("endereco"), 16, 171, 321, 46, 13, Texto, true)
    ui.texto(p, if (usuario.optBoolean("cuidador")) "Meu cadastro de cuidador" else "Meus Pets Cadastrados", 20, 508, 353, 27, 18, Texto, true)
    if (usuario.optBoolean("cuidador")) {
        val c = ui.caixa(p, 20, 545, 353, 71)
        ui.texto(c, usuario.optString("especialidade"), 16, 9, 321, 24, 16, Roxo, true)
        ui.texto(c, "${usuario.optString("experiencia")} anos de experiência", 16, 35, 321, 26, 14, Cinza)
    } else {
        val lista = dados.pets()
        lista.take(2).forEachIndexed { i, pet ->
            val c = ui.caixa(p, 20 + i * 182, 545, 171, 71)
            val avatar = ui.caixa(c, 10, 11, 48, 48, raio = 24)
            avatar.clipToOutline = true
            foto(avatar, pet, 0, 0, 48, 48)
            ui.texto(c, pet.nome, 67, 14, 95, 22, 14, Texto, true)
            ui.texto(c, "${pet.raca}\n${pet.idade} anos", 67, 36, 95, 29, 10, Cinza)
            c.setOnClickListener { petEditando = pet; fotoPet = pet.foto; mostrar("pet_form") }
        }
        if (lista.isEmpty()) ui.texto(p, "Você ainda não cadastrou um pet.", 20, 548, 353, 60, 14, Cinza, centro = true)
            .setOnClickListener { mostrar("pets") }
    }
    ui.botao(p, "Editar Perfil", 20, 639, 353, 40, true) { mostrar("editar_perfil") }
    ui.botao(p, "Sair da Conta", 20, 691, 353, 40) {
        AlertDialog.Builder(this).setTitle("Sair da conta?")
            .setPositiveButton("Sair") { _, _ -> dados.sair(); mostrar("login") }
            .setNegativeButton("Voltar", null).show()
    }.apply { setTextColor(0xFFE25050.toInt()); background = ui.fundo(0xFFFFF0F0.toInt(), 12) }
}

fun MainActivity.editarPerfil() {
    val usuario = dados.perfil()
    val p = pagina(altura.coerceAtLeast(720))
    voltar(p) { mostrar("perfil") }
    ui.texto(p, "Editar Perfil", 60, 20, 273, 38, 23, Texto, true, true)
    ui.imagem(p, R.drawable.perfil_banner, 24, 85, 345, 110)
    ui.texto(p, "Nome completo", 24, 222, 345, 27, 15, Texto, true)
    val nome = ui.campo(p, "Seu nome", 24, 251, 345, valor = usuario.optString("nome"))
    ui.texto(p, "Telefone", 24, 315, 345, 27, 15, Texto, true)
    val telefone = ui.campo(p, "Telefone com DDD", 24, 344, 345, tipo = InputType.TYPE_CLASS_PHONE, valor = usuario.optString("telefone"))
    ui.texto(p, "Endereço", 24, 408, 345, 27, 15, Texto, true)
    val endereco = ui.campo(p, "Seu endereço", 24, 437, 345, 70, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE, usuario.optString("endereco"))
    ui.botao(p, "Salvar Alterações", 24, 576, 345) {
        if (nome.text.isBlank() || telefone.text.count { it.isDigit() } < 10 || endereco.text.isBlank()) aviso("Preencha o nome, telefone com DDD e endereço.")
        else {
            dados.salvarPerfil(nome.text.toString(), telefone.text.toString(), endereco.text.toString())
            aviso("Perfil atualizado!")
            mostrar("perfil")
        }
    }
}

fun MainActivity.profissional() {
    val p = pagina(750, 4)
    voltar(p) { mostrar("home") }
    ui.texto(p, "Detalhes do Profissional", 69, 20, 266, 29, 20, Texto, true)
    val favorito = ui.caixa(p, 335, 10, 48, 44, Color.TRANSPARENT, 0)
    val coracao = ui.icone(favorito, R.drawable.ic_coracao, 12, 10, 25, if (dados.favorito) Roxo else Texto)
    favorito.contentDescription = "Favoritar profissional"
    favorito.setOnClickListener { dados.favorito = !dados.favorito; coracao.setColorFilter(if (dados.favorito) Roxo else Texto) }
    ui.imagem(p, R.drawable.bruno, 142, 69, 110, 111, "Bruno Oliveira com um cachorro")
    ui.texto(p, "Bruno Oliveira", 60, 191, 195, 32, 22, Texto, true, true)
    ui.texto(p, "PRO", 258, 195, 38, 22, 11, 0xFFFF7D00.toInt(), true, true).background = ui.fundo(0xFFFFF1E3.toInt(), 12)
    ui.texto(p, "☆ 4.9  (142 avaliações)", 85, 224, 223, 26, 14, Cinza, centro = true)
    ui.texto(p, "Especialidades", 20, 264, 353, 27, 16, Texto, true)
    val x = listOf(20, 132, 230, 20)
    val w = listOf(103, 89, 120, 113)
    servicos.forEachIndexed { i, s ->
        ui.texto(p, s, x[i], if (i < 3) 293 else 330, w[i], 27, 13, Roxo, centro = true).apply {
            background = ui.fundo(Lilas, 14, Linha)
            setOnClickListener { novoAgendamento(s) }
        }
    }
    ui.texto(p, "Sobre mim", 20, 380, 353, 27, 16, Texto, true)
    ui.texto(p, "Apaixonado por animais desde a infância. Trabalho como PetSitter certificado há mais de 4 anos, garantindo conforto, carinho e rotinas de exercícios saudáveis para o seu melhor amigo.",
        20, 411, 353, 81, 14, Cinza)
    ui.texto(p, "Avaliações Recentes", 20, 515, 280, 25, 16, Texto, true)
    ui.texto(p, "Ver todas", 300, 515, 73, 25, 13, Roxo, true, true).setOnClickListener {
        AlertDialog.Builder(this).setTitle("Avaliações de exemplo")
            .setMessage("Camila Souza · 5 estrelas\nExcelente profissional! Cuidou muito bem da minha gata Mel durante as férias. Mandou fotos e atualizações diariamente.")
            .setPositiveButton("Fechar", null).show()
    }
    val avaliacao = ui.caixa(p, 20, 549, 353, 119, raio = 15, borda = Linha)
    ui.imagem(avaliacao, R.drawable.camila, 12, 12, 30, 30)
    ui.texto(avaliacao, "Camila Souza", 52, 10, 210, 23, 14, Texto, true)
    ui.texto(avaliacao, "Há 3 dias", 52, 32, 210, 18, 11, Cinza)
    ui.texto(avaliacao, "☆☆☆☆☆", 270, 15, 74, 24, 16, 0xFFFFB300.toInt(), true)
    ui.texto(avaliacao, "\"Excelente profissional! Cuidou muito bem da minha gata Mel durante as férias. Mandou fotos e atualizações diariamente.\"", 12, 54, 329, 56, 13, Cinza)
    ui.botao(p, "Agendar Serviço", 20, 692, 353) { novoAgendamento() }
}
