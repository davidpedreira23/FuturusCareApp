package com.futuruscare.app

import android.app.AlertDialog
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.FrameLayout

fun MainActivity.home() {
    val p = pagina(930, 0)
    val banner = ui.caixa(p, 20, 35, 353, 214, raio = 20)
    ui.imagem(banner, R.drawable.home_animals, 178, 59, 174, 151)
    ui.texto(banner, "Cuidado e carinho\npara o seu pet.", 20, 21, 198, 58, 22, Texto, true)
    ui.texto(banner, "Encontre quem cuida\ncomo você.", 20, 85, 180, 42, 14, Cinza)
    ui.botao(banner, "Buscar cuidador", 20, 132, 157, 44) { buscarCuidadores() }
        .setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 13 * ui.escala)
    val busca = ui.campo(p, "Buscar cuidadores, serviços...", 20, 259, 353, 47)
    busca.setPadding(ui.px(42), 0, ui.px(14), 0)
    ui.icone(p, R.drawable.ic_busca, 34, 273, 19)
    busca.isFocusable = false
    busca.setOnClickListener { buscarCuidadores() }
    ui.texto(p, "Serviços Populares", 20, 321, 270, 29, 18, Roxo, true)
    ui.texto(p, "Ver todos", 303, 321, 70, 29, 12, Cinza, centro = true).setOnClickListener { buscarCuidadores() }
    servicos.take(2).forEachIndexed { i, servico ->
        val c = ui.caixa(p, 20 + i * 181, 358, 172, 111, raio = 17)
        ui.caixa(c, 12, 12, 40, 40, 0xFFFFF8DF.toInt(), 12)
        ui.icone(c, if (i == 0) R.drawable.ic_pets else R.drawable.ic_home, 20, 20, 24, Roxo)
        ui.texto(c, servico, 12, 63, 152, 23, 14, Texto, true)
        ui.texto(c, if (i == 0) "Passeios com carinho" else "Cuidado em casa", 12, 86, 152, 20, 12, Cinza)
        c.setOnClickListener { buscarCuidadores(servico) }
    }
    ui.texto(p, "Próximos Agendamentos", 20, 486, 353, 29, 18, Texto, true)
    val compromissos = dados.compromissos()
    if (compromissos.isEmpty()) {
        ui.texto(p, "Você ainda não tem agendamentos.", 20, 533, 353, 38, 15, Cinza, centro = true)
        ui.botao(p, "Agendar um cuidado", 20, 586, 353) { novoAgendamento() }
    } else compromissos.take(2).forEachIndexed { i, c -> cartaoCompromisso(p, c, 521 + i * 124) }
    ui.texto(p, "Cuidadores para seu pet", 20, 775, 353, 32, 18, Texto, true)
    val c = ui.caixa(p, 20, 817, 353, 82)
    ui.imagem(c, R.drawable.bruno, 14, 12, 56, 56, "Bruno Oliveira")
    ui.texto(c, "Bruno Oliveira", 85, 15, 250, 23, 16, Texto, true)
    ui.texto(c, "★ 4,9  ·  Dog Walking e Pet Sitting", 85, 42, 250, 25, 12, Cinza)
    c.setOnClickListener { mostrar("profissional") }
}

fun MainActivity.buscarCuidadores(filtro: String = "") {
    val opcoes = if (filtro.isEmpty()) arrayOf("Bruno Oliveira · Dog Walking", "Ana Paula · Pet Sitting", "Clínica Pet Salles · Banho & Tosa", "Ver todos os serviços")
        else arrayOf("Bruno Oliveira · $filtro", "Agendar $filtro")
    AlertDialog.Builder(this).setTitle(if (filtro.isEmpty()) "Encontre um cuidado" else filtro)
        .setItems(opcoes) { _, i ->
            if (i == 0) mostrar("profissional")
            else if (i == 3) AlertDialog.Builder(this).setTitle("Serviços").setItems(servicos.toTypedArray()) { _, j -> buscarCuidadores(servicos[j]) }.show()
            else novoAgendamento(servicoInicial = filtro.ifEmpty { if (i == 1) "Pet Sitting" else "Banho & Tosa" })
        }.setNegativeButton("Voltar", null).show()
}

fun MainActivity.cartaoCompromisso(p: FrameLayout, c: Compromisso, y: Int) {
    val card = ui.caixa(p, 20, y, 353, 111, raio = 18, borda = Linha)
    ui.imagem(card, if (c.profissional.startsWith("Bruno")) R.drawable.avatar_bruno else R.drawable.ana, 16, 15, 40, 40)
    ui.texto(card, c.profissional, 66, 14, 190, 24, 14, Texto, true)
    ui.texto(card, "Pet: ${c.pet}", 66, 38, 180, 20, 12, Cinza)
    val estado = ui.caixa(card, 254, 25, 83, 22, if (c.confirmado) 0xFFE2FAE2.toInt() else 0xFFFFF3C9.toInt(), 12)
    ui.texto(estado, if (c.confirmado) "Confirmado" else "Pendente", 0, 0, 83, 22, 11,
        if (c.confirmado) 0xFF18732A.toInt() else 0xFFA05100.toInt(), true, true)
    ui.caixa(card, 16, 66, 321, 1, Linha, 0)
    ui.icone(card, R.drawable.ic_relogio, 16, 80, 16, Roxo)
    ui.texto(card, "${c.data.take(5)} às ${c.horario}", 38, 77, 196, 26, 13, Texto, true)
    ui.texto(card, c.servico, 238, 77, 100, 26, 12, Roxo, true, true)
    card.contentDescription = "Agendamento com ${c.profissional}, ${c.data}, ${c.horario}"
    card.setOnClickListener {
        AlertDialog.Builder(this).setTitle(c.servico)
            .setMessage("${c.profissional}\nPet: ${c.pet}\n${c.data} às ${c.horario}\n${if (c.confirmado) "Confirmado" else "Pendente"}")
            .setPositiveButton("Conversar") { _, _ -> contato = c.profissional; mostrar("conversa") }
            .setNeutralButton("Excluir") { _, _ ->
                AlertDialog.Builder(this).setTitle("Excluir agendamento?").setMessage("Ele será removido da agenda deste aparelho.")
                    .setPositiveButton("Excluir") { _, _ -> dados.salvarCompromissos(dados.compromissos().filter { it.id != c.id }); mostrar(telaAtual) }
                    .setNegativeButton("Voltar", null).show()
            }.setNegativeButton("Fechar", null).show()
    }
}

fun MainActivity.pets() {
    val lista = dados.pets()
    val p = pagina((100 + lista.size * 252).coerceAtLeast(altura - 90), 1)
    titulo(p, "Meus Pets")
    val sino = ui.caixa(p, 337, 20, 36, 36, raio = 30)
    ui.icone(sino, R.drawable.ic_sino, 7, 7, 22, Texto)
    sino.contentDescription = "Ver agendamentos"
    sino.setOnClickListener { mostrar("agenda") }
    lista.forEachIndexed { i, pet ->
        val c = ui.caixa(p, 20, 77 + i * 252, 352, 234, raio = 20, borda = Linha)
        c.clipToOutline = true
        foto(c, pet, 0, 0, 352, 159)
        ui.texto(c, pet.nome, 16, 172, 260, 29, 18, Texto, true)
        ui.texto(c, "${pet.raca} • ${pet.idade} anos", 16, 200, 268, 26, 14, Cinza)
        val peso = ui.caixa(c, 284, 183, 53, 28, Lilas, 14)
        ui.texto(peso, "${pet.peso}kg", 0, 0, 53, 28, 12, Roxo, true, true)
        c.setOnClickListener { petEditando = pet; fotoPet = pet.foto; mostrar("pet_form") }
    }
    if (lista.isEmpty()) ui.texto(p, "Cadastre seu primeiro pet\npara começar a cuidar dele por aqui.", 32, 210, 329, 80, 17, Cinza, centro = true)
    ui.botao(raiz, "+", 313, altura - 152, 56, 56) {
        petEditando = null; fotoPet = ""; mostrar("pet_form")
    }.apply { background = ui.fundo(Roxo, 30); setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 32 * ui.escala); contentDescription = "Cadastrar novo pet"; ancorarRodape(this, 96) }
}

fun MainActivity.conversas() {
    val p = pagina(altura - 90, 3)
    titulo(p, "Mensagens")
    val busca = ui.campo(p, "Buscar conversas...", 20, 76, 353, 41)
    busca.setPadding(ui.px(45), 0, ui.px(14), 0)
    ui.icone(p, R.drawable.ic_busca, 35, 87, 20)
    val lista = ui.caixa(p, 20, 137, 353, 242, raio = 18, borda = Linha)
    fun preencher(filtro: String) {
        lista.removeAllViews()
        val nomes = listOf("Bruno Oliveira", "Ana Paula (Sitter)", "Clínica Pet Salles")
        val avatares = listOf(R.drawable.chat_bruno, R.drawable.chat_ana, R.drawable.clinica)
        val resumos = listOf("Claro! Posso levar o Pipoca para passear.", "Olá Mariana, a ração do Floco já está aqui.", "O agendamento da vacina foi confirmado.")
        var linha = 0
        nomes.forEachIndexed { i, nome ->
            if (nome.contains(filtro, true)) {
                val c = ui.caixa(lista, 0, linha * 80, 353, 80, Color.TRANSPARENT, 0)
                ui.imagem(c, avatares[i], 16, 16, 48, 48)
                ui.texto(c, nome, 76, 16, 244, 24, 16, Texto, true)
                val resumo = dados.mensagens(nome).lastOrNull() ?: resumos[i]
                ui.texto(c, resumo, 76, 43, 259, 22, 13, Cinza).apply { maxLines = 1; ellipsize = android.text.TextUtils.TruncateAt.END }
                if (linha > 0) ui.caixa(c, 0, 0, 353, 1, Linha, 0)
                c.setOnClickListener { contato = nome; mostrar("conversa") }
                linha++
            }
        }
        if (linha == 0) ui.texto(lista, "Nenhuma conversa encontrada.", 16, 25, 321, 45, 15, Cinza, centro = true)
    }
    preencher("")
    busca.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { preencher(s.toString()) }
        override fun afterTextChanged(s: Editable?) {}
    })
}

fun MainActivity.conversa() {
    val mensagens = dados.mensagens(contato)
    val p = pagina((220 + mensagens.size * 94).coerceAtLeast(altura - 110))
    voltar(p) { mostrar("chat") }
    ui.texto(p, contato, 63, 16, 310, 37, 20, Texto, true)
    ui.texto(p, "Conversa de demonstração · mensagens salvas neste celular", 20, 65, 353, 36, 12, Cinza, centro = true)
    ui.texto(p, "Olá! Como posso ajudar com seu pet?", 20, 119, 300, 65, 15).apply {
        background = ui.fundo(Color.WHITE)
        setPadding(ui.px(14), 0, ui.px(14), 0)
    }
    mensagens.forEachIndexed { i, texto ->
        ui.texto(p, texto, 64, 200 + i * 94, 309, 78, 15, Roxo).apply {
            background = ui.fundo(Lilas)
            setPadding(ui.px(14), ui.px(8), ui.px(14), ui.px(8))
        }
    }
    val rodape = ui.caixa(raiz, 0, altura - 86, 393, 86, Fundo, 0)
    ancorarRodape(rodape, 0)
    (raiz.getChildAt(0).layoutParams as FrameLayout.LayoutParams).bottomMargin = ui.px(86)
    val entrada = ui.campo(rodape, "Escreva uma mensagem...", 16, 14, 271, 48)
    entrada.filters = arrayOf(android.text.InputFilter.LengthFilter(180))
    ui.botao(rodape, "Enviar", 297, 14, 80) {
        val texto = entrada.text.toString().trim()
        if (texto.isNotEmpty()) { dados.enviarMensagem(contato, texto); mostrar("conversa") }
    }
}
