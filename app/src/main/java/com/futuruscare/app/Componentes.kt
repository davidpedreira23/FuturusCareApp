package com.futuruscare.app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*

val Roxo = Color.rgb(94, 23, 235)
val Fundo = Color.rgb(240, 238, 223)
val Texto = Color.rgb(48, 48, 48)
val Cinza = Color.rgb(117, 117, 125)
val Linha = Color.rgb(226, 225, 231)
val Lilas = Color.rgb(244, 238, 255)

class Componentes(val contexto: Context) {
    val escala = contexto.resources.displayMetrics.widthPixels / 393f
    private val fonteTitulo = contexto.resources.getFont(R.font.quicksand)
    fun px(valor: Int) = (valor * escala).toInt()

    fun fundo(cor: Int, raio: Int = 16, borda: Int? = null): GradientDrawable =
        GradientDrawable().apply {
            setColor(cor)
            cornerRadius = px(raio).toFloat()
            if (borda != null) setStroke(px(1).coerceAtLeast(1), borda)
        }

    fun painel() = FrameLayout(contexto)

    fun colocar(pai: FrameLayout, view: View, x: Int, y: Int, largura: Int, altura: Int): View {
        pai.addView(view, FrameLayout.LayoutParams(px(largura), px(altura)).apply {
            leftMargin = px(x)
            topMargin = px(y)
        })
        return view
    }

    fun caixa(pai: FrameLayout, x: Int, y: Int, largura: Int, altura: Int,
              cor: Int = Color.WHITE, raio: Int = 18, borda: Int? = null): FrameLayout =
        painel().also {
            it.background = fundo(cor, raio, borda)
            colocar(pai, it, x, y, largura, altura)
        }

    fun texto(pai: FrameLayout, valor: String, x: Int, y: Int, largura: Int, altura: Int,
              tamanho: Int = 16, cor: Int = Texto, negrito: Boolean = false,
              centro: Boolean = false): TextView = TextView(contexto).also {
        it.text = valor
        it.setTextColor(cor)
        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, tamanho * escala)
        it.typeface = Typeface.create("sans-serif", if (negrito) Typeface.BOLD else Typeface.NORMAL)
        if (tamanho >= 18) it.typeface = Typeface.create(fonteTitulo, if (negrito) Typeface.BOLD else Typeface.NORMAL)
        it.includeFontPadding = false
        it.gravity = if (centro) Gravity.CENTER else Gravity.CENTER_VERTICAL
        colocar(pai, it, x, y, largura, altura)
    }

    fun botao(pai: FrameLayout, titulo: String, x: Int, y: Int, largura: Int, altura: Int = 48,
              contorno: Boolean = false, acao: () -> Unit): TextView =
        texto(pai, titulo, x, y, largura, altura, 16,
            if (contorno) Roxo else Color.WHITE, true, true).also {
            val base = fundo(if (contorno) Color.WHITE else Roxo, 16, if (contorno) Roxo else null)
            it.background = RippleDrawable(ColorStateList.valueOf(0x225E17EB), base, null)
            it.isClickable = true
            it.isFocusable = true
            it.setOnClickListener { acao() }
            it.accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(host: View, info: android.view.accessibility.AccessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.className = "android.widget.Button"
                }
            }
        }

    fun imagem(pai: FrameLayout, recurso: Int, x: Int, y: Int, largura: Int, altura: Int,
               descricao: String? = null, recortar: Boolean = false): ImageView = ImageView(contexto).also {
        it.setImageResource(recurso)
        it.scaleType = if (recortar) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_XY
        it.contentDescription = descricao
        colocar(pai, it, x, y, largura, altura)
    }

    fun icone(pai: FrameLayout, recurso: Int, x: Int, y: Int, tamanho: Int = 24,
              cor: Int = Cinza): ImageView = imagem(pai, recurso, x, y, tamanho, tamanho).also {
        it.setColorFilter(cor)
    }

    fun campo(pai: FrameLayout, dica: String, x: Int, y: Int, largura: Int, altura: Int = 44,
              tipo: Int = android.text.InputType.TYPE_CLASS_TEXT, valor: String = ""): EditText =
        EditText(contexto).also {
            it.hint = dica
            it.setText(valor)
            it.inputType = tipo
            it.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            it.setTextColor(Texto)
            it.setHintTextColor(Cinza)
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15 * escala)
            it.background = fundo(Color.WHITE, 13, Linha)
            it.setPadding(px(16), px(6), px(14), px(6))
            it.isSingleLine = altura < 65
            if (altura >= 65) it.gravity = Gravity.TOP
            it.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
            colocar(pai, it, x, y, largura, altura)
        }

    fun selecao(pai: FrameLayout, opcoes: List<String>, x: Int, y: Int, largura: Int,
                selecionado: String = opcoes.first()): Spinner = Spinner(contexto).also {
        it.adapter = ArrayAdapter(contexto, android.R.layout.simple_spinner_dropdown_item, opcoes)
        it.background = fundo(Color.WHITE, 13, Linha)
        it.setSelection(opcoes.indexOf(selecionado).coerceAtLeast(0))
        colocar(pai, it, x, y, largura, 48)
    }
}
