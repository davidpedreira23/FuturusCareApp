package com.futuruscare.app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.text.InputType
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.Calendar
import java.util.Locale

fun MainActivity.formularioPet() {
    val pet = petEditando
    val h = altura.coerceAtLeast(810)
    val p = pagina(h)
    ui.texto(p, if (pet == null) "Cadastrar Novo Pet" else "Editar Pet", 55, 23, 283, 33, 23, Texto, true, true)
    voltar(p) { mostrar("pets") }
    val circulo = ui.caixa(p, 146, 73, 100, 100, Color.WHITE, 50)
    circulo.clipToOutline = true
    fotoView = if (fotoPet.isEmpty()) ui.icone(circulo, R.drawable.ic_camera, 35, 36, 30, Roxo)
        else foto(circulo, pet?.copy(foto = fotoPet) ?: Pet(nome = "Foto do pet", especie = "", raca = "", idade = "", peso = "", foto = fotoPet), 0, 0, 100, 100)
    circulo.contentDescription = "Adicionar foto do pet"
    circulo.setOnClickListener {
        if (fotoPet.isEmpty()) {
            circulo.removeAllViews()
            fotoView = ui.imagem(circulo, R.drawable.ic_camera, 0, 0, 100, 100, recortar = true)
        }
        escolherFoto()
    }
    ui.texto(p, "Adicionar Foto do Pet", 70, 178, 253, 30, 14, Roxo, true, true).setOnClickListener { circulo.performClick() }
    ui.texto(p, "Nome do Pet", 24, 216, 345, 24, 15, Texto, true)
    val nome = ui.campo(p, "ex: Thor", 24, 240, 345, 41, valor = pet?.nome ?: "")
    ui.texto(p, "Espécie", 24, 291, 167, 24, 15, Texto, true)
    ui.texto(p, "Raça", 202, 291, 167, 24, 15, Texto, true)
    val especie = ui.selecao(p, listOf("Cão", "Gato", "Outro"), 24, 317, 167, pet?.especie ?: "Cão")
    val raca = ui.campo(p, "Golden Retriever", 202, 317, 167, 58, valor = pet?.raca ?: "")
    ui.texto(p, "Idade (anos)", 24, 388, 167, 24, 15, Texto, true)
    ui.texto(p, "Peso (kg)", 202, 388, 167, 24, 15, Texto, true)
    val idade = ui.campo(p, "ex: 2", 24, 412, 167, 41, InputType.TYPE_CLASS_NUMBER, pet?.idade ?: "")
    val peso = ui.campo(p, "ex: 12.5", 202, 412, 167, 41, InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL, pet?.peso ?: "")
    ui.texto(p, "Observações Especiais (Alergias / Remédios)", 24, 465, 345, 24, 14, Texto, true)
    val observacoes = ui.campo(p, "ex: Alérgico a corantes vermelhos. Toma remédio de ouvido.", 24, 489, 345, 69,
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE, pet?.observacoes ?: "")
    ui.botao(p, if (pet == null) "Cadastrar Pet" else "Salvar Alterações", 24, h - 105, 345) {
        val pesoNumero = peso.text.toString().replace(',', '.').toDoubleOrNull()
        when {
            nome.text.isBlank() -> aviso("Informe o nome do pet.")
            raca.text.isBlank() -> aviso("Informe a raça ou escreva SRD.")
            idade.text.toString().toIntOrNull() !in 0..100 -> aviso("Informe uma idade válida.")
            pesoNumero == null || pesoNumero <= 0 || pesoNumero > 1000 -> aviso("Informe um peso válido.")
            else -> {
                dados.salvarPet(Pet(pet?.id ?: System.currentTimeMillis(), nome.text.toString().trim(), especie.selectedItem.toString(),
                    raca.text.toString().trim(), idade.text.toString(), peso.text.toString().replace(',', '.'), observacoes.text.toString(), fotoPet))
                aviso("Pet salvo!")
                mostrar("pets")
            }
        }
    }
}

fun MainActivity.agenda() {
    val lista = dados.compromissos()
    val h = (340 + lista.size * 124).coerceAtLeast(altura - 90)
    val p = pagina(h, 2)
    titulo(p, "Agendamentos")
    val calendario = ui.caixa(p, 20, 76, 353, 113, raio = 18, borda = Linha)
    val hoje = Calendar.getInstance()
    val meses = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    val mes = ui.texto(calendario, "${meses[hoje.get(Calendar.MONTH)]} ${hoje.get(Calendar.YEAR)}", 16, 12, 285, 27, 16, Texto, true)
    ui.icone(calendario, R.drawable.ic_agenda, 319, 16, 19)
    val dias = ui.painel()
    ui.colocar(calendario, dias, 10, 48, 333, 55)
    val compromissos = ui.painel()
    ui.colocar(p, compromissos, 0, 253, 393, (lista.size * 124).coerceAtLeast(130))
    fun mostrarLista(filtro: String? = null) {
        compromissos.removeAllViews()
        val filtrados = if (filtro == null) lista else lista.filter { it.data == filtro }
        filtrados.forEachIndexed { i, c -> cartaoCompromisso(compromissos, c, i * 124) }
        if (filtrados.isEmpty()) ui.texto(compromissos, "Nenhum compromisso nesta data.", 20, 10, 353, 65, 16, Cinza, centro = true)
    }
    fun atualizarSemana(selecionado: Calendar) {
        dias.removeAllViews()
        mes.text = "${meses[selecionado.get(Calendar.MONTH)]} ${selecionado.get(Calendar.YEAR)}"
        repeat(6) { i ->
            val dia = (selecionado.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, i - 2) }
            val c = ui.caixa(dias, i * 56 + 2, 1, 40, 50, if (i == 2) Roxo else Color.TRANSPARENT, 12)
            val semana = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")[dia.get(Calendar.DAY_OF_WEEK) - 1]
            ui.texto(c, semana, 0, 4, 40, 17, 11, if (i == 2) Color.WHITE else Cinza, centro = true)
            ui.texto(c, dia.get(Calendar.DAY_OF_MONTH).toString(), 0, 23, 40, 24, 16, if (i == 2) Color.WHITE else Texto, true, true)
            c.setOnClickListener {
                mostrarLista(String.format(Locale.US, "%02d/%02d/%04d", dia.get(Calendar.DAY_OF_MONTH), dia.get(Calendar.MONTH) + 1, dia.get(Calendar.YEAR)))
            }
        }
    }
    atualizarSemana(hoje)
    calendario.setOnClickListener {
        DatePickerDialog(this, { _, ano, mesNumero, dia ->
            val data = Calendar.getInstance().apply { set(ano, mesNumero, dia) }
            atualizarSemana(data)
            mostrarLista(String.format(Locale.US, "%02d/%02d/%04d", dia, mesNumero + 1, ano))
        }, hoje.get(Calendar.YEAR), hoje.get(Calendar.MONTH), hoje.get(Calendar.DAY_OF_MONTH)).show()
    }
    ui.texto(p, "Seus Compromissos", 20, 211, 265, 30, 18, Texto, true)
    ui.texto(p, "Ver todos", 285, 211, 88, 30, 12, Roxo, centro = true).setOnClickListener { mostrarLista() }
    mostrarLista()
    ui.botao(raiz, "Novo Agendamento", 16, altura - 166, 361) { novoAgendamento() }.also { ancorarRodape(it, 118) }
}

fun MainActivity.novoAgendamento(servicoInicial: String = "Dog Walking") {
    val pets = dados.pets()
    if (pets.isEmpty()) {
        AlertDialog.Builder(this).setTitle("Cadastre seu pet primeiro")
            .setMessage("O agendamento precisa estar ligado a um pet.")
            .setPositiveButton("Cadastrar pet") { _, _ -> petEditando = null; fotoPet = ""; mostrar("pet_form") }
            .setNegativeButton("Voltar", null).show()
        return
    }
    val painel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(ui.px(22), ui.px(12), ui.px(22), 0) }
    fun opcao(titulo: String, itens: List<String>): Spinner {
        painel.addView(TextView(this).apply { text = titulo; setTextColor(Texto); setPadding(0, ui.px(8), 0, 0) })
        return Spinner(this).also {
            it.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itens)
            painel.addView(it, LinearLayout.LayoutParams(-1, ui.px(48)))
        }
    }
    val pet = opcao("Pet", pets.map { it.nome })
    val profissional = opcao("Profissional", listOf("Bruno Oliveira", "Ana Paula (Sitter)", "Clínica Pet Salles"))
    val servico = opcao("Serviço", servicos)
    servico.setSelection(servicos.indexOf(servicoInicial).coerceAtLeast(0))
    val data = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 0) }
    fun dataTexto() = String.format(Locale.US, "%02d/%02d/%04d", data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH) + 1, data.get(Calendar.YEAR))
    fun horaTexto() = String.format(Locale.US, "%02d:%02d", data.get(Calendar.HOUR_OF_DAY), data.get(Calendar.MINUTE))
    val escolherData = android.widget.Button(this).apply { text = dataTexto(); isAllCaps = false }
    val escolherHora = android.widget.Button(this).apply { text = horaTexto(); isAllCaps = false }
    painel.addView(escolherData)
    painel.addView(escolherHora)
    escolherData.setOnClickListener {
        DatePickerDialog(this, { _, ano, mes, dia -> data.set(ano, mes, dia); escolherData.text = dataTexto() },
            data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH)).apply { datePicker.minDate = System.currentTimeMillis() - 1000 }.show()
    }
    escolherHora.setOnClickListener {
        TimePickerDialog(this, { _, h, m -> data.set(Calendar.HOUR_OF_DAY, h); data.set(Calendar.MINUTE, m); escolherHora.text = horaTexto() },
            data.get(Calendar.HOUR_OF_DAY), data.get(Calendar.MINUTE), true).show()
    }
    val dialogo = AlertDialog.Builder(this).setTitle("Novo Agendamento").setView(painel)
        .setPositiveButton("Salvar", null).setNegativeButton("Voltar", null).create()
    dialogo.setOnShowListener {
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (data.timeInMillis <= System.currentTimeMillis()) aviso("Escolha uma data e um horário futuros.")
            else {
                val lista = dados.compromissos()
                lista.add(Compromisso(profissional = profissional.selectedItem.toString(), pet = pet.selectedItem.toString(),
                    servico = servico.selectedItem.toString(), data = dataTexto(), horario = horaTexto()))
                dados.salvarCompromissos(lista)
                dialogo.dismiss()
                mostrar("agenda")
            }
        }
    }
    dialogo.show()
}
