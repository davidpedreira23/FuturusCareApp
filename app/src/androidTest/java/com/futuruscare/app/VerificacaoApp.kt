package com.futuruscare.app

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import java.io.File

class VerificacaoApp : Instrumentation() {
    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        start()
    }

    override fun onStart() {
        val resultado = Bundle()
        var app: MainActivity? = null
        try {
            val dados = DadosLocais(targetContext)
            check(!dados.entrar("mariana.silva@exemplo.com", "incorreta", false))
            check(!dados.entrar("mariana.silva@exemplo.com", "12345678", true))
            check(dados.entrar("mariana.silva@exemplo.com", "12345678", false))
            app = startActivitySync(Intent(targetContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as MainActivity
            waitForIdleSync()
            val atividade = app
            fun tela(nome: String) {
                runOnMainSync { atividade.mostrar(nome) }
                waitForIdleSync()
                Thread.sleep(900)
                val imagem = uiAutomation.takeScreenshot()
                File(targetContext.filesDir, "teste_$nome.png").outputStream().use { imagem.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
                imagem.recycle()
            }
            listOf("intro", "valores", "escolha", "login", "cadastro", "home", "pets", "agenda", "chat", "conversa", "perfil", "profissional", "editar_perfil", "pet_form").forEach { tela(it) }
            runOnMainSync { atividade.cuidador = true }
            tela("cadastro")
            runOnMainSync { atividade.cuidador = false; atividade.petEditando = null; atividade.fotoPet = "" }
            tela("pet_form")
            runOnMainSync {
                val campos = descendentes(atividade.raiz).filterIsInstance<EditText>()
                campos[0].setText("Thor")
                campos[1].setText("SRD")
                campos[2].setText("2")
                campos[3].setText("12.5")
                campos[4].setText("Precisa passear pela manhã.")
                descendentes(atividade.raiz).filterIsInstance<TextView>().first { it.text.toString() == "Cadastrar Pet" }.performClick()
            }
            waitForIdleSync()
            check(atividade.telaAtual == "pets")
            check(DadosLocais(targetContext).pets().any { it.nome == "Thor" && it.peso == "12.5" })
            val email = "teste_${System.currentTimeMillis()}@exemplo.com"
            check(dados.cadastrar("Teste", email, "11999999999", "Rua Teste, 10", "senha1234", false, "", ""))
            check(dados.pets().isEmpty())
            dados.salvarPet(Pet(50, "Luna", "Gato", "SRD", "1", "3"))
            dados.salvarPet(Pet(50, "Luna", "Gato", "SRD", "2", "4"))
            check(dados.pets().size == 1 && dados.pets()[0].idade == "2")
            dados.enviarMensagem("Bruno Oliveira", "Olá")
            dados.salvarCompromissos(listOf(Compromisso(70, "Bruno Oliveira", "Luna", "Pet Sitting", "20/10/2026", "10:00")))
            dados.salvarPerfil("Novo Nome", "11888888888", "Rua Nova, 20")
            dados.sair()
            val recarregado = DadosLocais(targetContext)
            check(!recarregado.conectado)
            check(recarregado.entrar(email, "senha1234", false))
            check(recarregado.pets().single().nome == "Luna")
            check(recarregado.mensagens("Bruno Oliveira") == listOf("Olá"))
            check(recarregado.compromissos().single().id == 70L)
            check(recarregado.perfil().getString("nome") == "Novo Nome")
            check(recarregado.entrar("mariana.silva@exemplo.com", "12345678", false))
            check(recarregado.mensagens("Bruno Oliveira").none { it == "Olá" })
            resultado.putString("stream", "Telas, formulário de pet, autenticação, isolamento de contas e persistência: OK.\n")
            finish(Activity.RESULT_OK, resultado)
        } catch (erro: Throwable) {
            resultado.putString("stream", erro.stackTraceToString())
            finish(Activity.RESULT_CANCELED, resultado)
        } finally {
            app?.let { runOnMainSync { it.finish() } }
        }
    }

    private fun descendentes(view: View): List<View> = listOf(view) +
        if (view is ViewGroup) (0 until view.childCount).flatMap { descendentes(view.getChildAt(it)) } else emptyList()
}
