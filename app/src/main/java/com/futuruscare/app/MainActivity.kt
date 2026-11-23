package com.futuruscare.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import java.io.File

class MainActivity : Activity() {
    lateinit var ui: Componentes
    lateinit var dados: DadosLocais
    lateinit var raiz: FrameLayout
    var altura = 852
    var telaAtual = "abertura"
    var cuidador = false
    var petEditando: Pet? = null
    var fotoPet = ""
    var fotoView: ImageView? = null
    var contato = "Bruno Oliveira"
    val emailCadastroId = View.generateViewId()
    val senhaCadastroId = View.generateViewId()
    val servicos = listOf("Dog Walking", "Pet Sitting", "Hospedagem", "Banho & Tosa")
    private val relogio = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Fundo
        window.navigationBarColor = Fundo
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        ui = Componentes(this)
        dados = DadosLocais(this)
        raiz = FrameLayout(this)
        raiz.setBackgroundColor(Fundo)
        setContentView(raiz)
        cuidador = savedInstanceState?.getBoolean("cuidador") ?: false
        contato = savedInstanceState?.getString("contato") ?: contato
        raiz.post {
            altura = (raiz.height / ui.escala).toInt().coerceAtLeast(680)
            mostrar(savedInstanceState?.getString("tela") ?: if (dados.conectado) "home" else "abertura")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("tela", if (telaAtual == "pet_form") "pets" else telaAtual)
        outState.putBoolean("cuidador", cuidador)
        outState.putString("contato", contato)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        relogio.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun mostrar(tela: String) {
        telaAtual = tela
        raiz.removeAllViews()
        when (tela) {
            "abertura" -> {
                abertura()
                relogio.postDelayed({ if (telaAtual == "abertura") mostrar("intro") }, 1300)
            }
            "intro" -> introducao(false)
            "valores" -> introducao(true)
            "escolha" -> acesso(true)
            "login" -> acesso(false)
            "cadastro" -> cadastro()
            "home" -> home()
            "pets" -> pets()
            "agenda" -> agenda()
            "chat" -> conversas()
            "conversa" -> conversa()
            "perfil" -> perfil()
            "profissional" -> profissional()
            "editar_perfil" -> editarPerfil()
            "pet_form" -> formularioPet()
        }
    }

    fun pagina(conteudoAltura: Int = altura, aba: Int = -1): FrameLayout {
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
            clipToPadding = false
        }
        raiz.addView(scroll, FrameLayout.LayoutParams(-1, -1).apply {
            if (aba >= 0) bottomMargin = ui.px(90)
        })
        val conteudo = ui.painel()
        scroll.addView(conteudo, FrameLayout.LayoutParams(-1, ui.px(conteudoAltura)))
        if (aba >= 0) navegacao(aba)
        return conteudo
    }

    fun navegacao(ativa: Int) {
        val barra = ui.caixa(raiz, 3, altura - 86, 387, 73, raio = 25)
        ancorarRodape(barra, 13)
        barra.elevation = ui.px(2).toFloat()
        val titulos = listOf("Home", "Meus Pets", "Agenda", "Chat", "Perfil")
        val telas = listOf("home", "pets", "agenda", "chat", "perfil")
        val icones = listOf(R.drawable.ic_home, R.drawable.ic_pets, R.drawable.ic_agenda, R.drawable.ic_chat, R.drawable.ic_perfil)
        titulos.forEachIndexed { i, titulo ->
            val x = 13 + i * 71
            val area = ui.caixa(barra, x, 4, 71, 65, Color.TRANSPARENT, 0)
            ui.icone(area, icones[i], 24, 13, 24, if (ativa == i) Roxo else Cinza)
            ui.texto(area, titulo, 0, 42, 71, 18, 11, if (ativa == i) Roxo else Cinza, ativa == i, true)
            if (ativa == i) ui.caixa(area, 20, 62, 32, 3, Roxo, 2)
            area.contentDescription = titulo
            area.isFocusable = true
            area.setOnClickListener { mostrar(telas[i]) }
        }
    }

    fun titulo(p: FrameLayout, texto: String) = ui.texto(p, texto, 20, 22, 315, 38, 28, Roxo, true)

    fun ancorarRodape(view: View, margem: Int) {
        (view.layoutParams as FrameLayout.LayoutParams).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.LEFT
            topMargin = 0
            bottomMargin = ui.px(margem)
        }.also { view.layoutParams = it }
    }

    fun aviso(texto: String) = Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()

    fun voltar(p: FrameLayout, acao: () -> Unit) {
        val alvo = ui.caixa(p, 10, 10, 44, 44, Color.TRANSPARENT, 0)
        ui.icone(alvo, R.drawable.ic_voltar, 11, 11, 22, Texto)
        alvo.contentDescription = "Voltar"
        alvo.setOnClickListener { acao() }
    }

    fun foto(p: FrameLayout, pet: Pet, x: Int, y: Int, w: Int, h: Int): ImageView {
        val recurso = when (pet.foto) { "pipoca" -> R.drawable.pipoca; "floco" -> R.drawable.floco; else -> R.drawable.ic_pets }
        return ui.imagem(p, recurso, x, y, w, h, pet.nome, true).also {
            if (pet.foto.isBlank()) {
                val tamanho = minOf(40, w / 2, h / 2)
                it.scaleType = ImageView.ScaleType.FIT_CENTER
                it.setPadding(ui.px((w - tamanho) / 2), ui.px((h - tamanho) / 2), ui.px((w - tamanho) / 2), ui.px((h - tamanho) / 2))
                it.setBackgroundColor(Lilas)
                it.setColorFilter(Roxo)
            }
            if (pet.foto.isNotBlank() && pet.foto != "pipoca" && pet.foto != "floco") {
                val arquivo = File(filesDir, pet.foto)
                if (arquivo.exists()) it.setImageBitmap(BitmapFactory.decodeFile(arquivo.path))
            }
        }
    }

    fun escolherFoto() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 10 || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, limites) }
            var amostra = 1
            while (limites.outWidth / amostra > 1200 || limites.outHeight / amostra > 1200) amostra *= 2
            val imagem = contentResolver.openInputStream(uri).use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = amostra })
            } ?: return aviso("Não foi possível abrir a foto.")
            fotoPet = "pet_${System.currentTimeMillis()}.jpg"
            File(filesDir, fotoPet).outputStream().use { imagem.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, it) }
            fotoView?.setImageBitmap(imagem)
        } catch (_: Exception) { aviso("Não foi possível abrir a foto.") }
    }

    @Deprecated("Compatibilidade com Android 8 e versões posteriores")
    override fun onBackPressed() {
        when (telaAtual) {
            "intro", "abertura", "home" -> super.onBackPressed()
            "valores" -> mostrar("intro")
            "escolha", "login" -> mostrar("valores")
            "cadastro" -> mostrar("escolha")
            "pet_form" -> mostrar("pets")
            "editar_perfil" -> mostrar("perfil")
            "conversa" -> mostrar("chat")
            else -> mostrar("home")
        }
    }
}
