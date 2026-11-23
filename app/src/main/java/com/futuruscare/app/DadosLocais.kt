package com.futuruscare.app

import android.content.Context
import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class Pet(
    val id: Long = System.currentTimeMillis(),
    val nome: String,
    val especie: String,
    val raca: String,
    val idade: String,
    val peso: String,
    val observacoes: String = "",
    val foto: String = ""
)

data class Compromisso(
    val id: Long = System.currentTimeMillis(),
    val profissional: String,
    val pet: String,
    val servico: String,
    val data: String,
    val horario: String,
    val confirmado: Boolean = false
)

class DadosLocais(context: Context) {
    private val preferencias = context.getSharedPreferences("futuruscare", Context.MODE_PRIVATE)
    private var usuario = preferencias.getString("sessao", "") ?: ""
    val conectado get() = usuario.isNotBlank()

    init {
        if (!preferencias.contains("contas")) {
            val conta = JSONObject().apply {
                put("nome", "Mariana Silva")
                put("email", "mariana.silva@exemplo.com")
                put("telefone", "(11) 99888-7766")
                put("endereco", "Alameda Lorena, 1420 - Jardins, São Paulo - SP")
                put("cuidador", false)
                put("especialidade", "Dog Walking")
                put("experiencia", "4")
                put("senha", criarSenha("12345678"))
            }
            preferencias.edit().putString("contas", JSONArray().put(conta).toString()).apply()
        }
    }

    fun cadastrar(nome: String, email: String, telefone: String, endereco: String,
                  senha: String, cuidador: Boolean, especialidade: String, experiencia: String): Boolean {
        val contas = JSONArray(preferencias.getString("contas", "[]"))
        val chave = email.trim().lowercase()
        for (i in 0 until contas.length()) {
            if (contas.getJSONObject(i).getString("email").equals(chave, true)) return false
        }
        contas.put(JSONObject().apply {
            put("nome", nome.trim())
            put("email", chave)
            put("telefone", telefone.trim())
            put("endereco", endereco.trim())
            put("senha", criarSenha(senha))
            put("cuidador", cuidador)
            put("especialidade", especialidade)
            put("experiencia", experiencia)
        })
        preferencias.edit().putString("contas", contas.toString()).apply()
        iniciarSessao(chave)
        return true
    }

    fun entrar(email: String, senha: String, cuidador: Boolean): Boolean {
        val contas = JSONArray(preferencias.getString("contas", "[]"))
        for (i in 0 until contas.length()) {
            val conta = contas.getJSONObject(i)
            if (conta.getString("email").equals(email.trim(), true) &&
                conta.optBoolean("cuidador") == cuidador && conferirSenha(senha, conta.getString("senha"))) {
                iniciarSessao(conta.getString("email"))
                return true
            }
        }
        return false
    }

    private fun iniciarSessao(email: String) {
        usuario = email
        preferencias.edit().putString("sessao", usuario).apply()
    }

    fun sair() {
        usuario = ""
        preferencias.edit().remove("sessao").apply()
    }

    fun perfil(): JSONObject {
        val contas = JSONArray(preferencias.getString("contas", "[]"))
        for (i in 0 until contas.length()) {
            if (contas.getJSONObject(i).getString("email") == usuario) return contas.getJSONObject(i)
        }
        return JSONObject()
    }

    fun salvarPerfil(nome: String, telefone: String, endereco: String) {
        val contas = JSONArray(preferencias.getString("contas", "[]"))
        for (i in 0 until contas.length()) {
            val conta = contas.getJSONObject(i)
            if (conta.getString("email") == usuario) {
                conta.put("nome", nome.trim()).put("telefone", telefone.trim()).put("endereco", endereco.trim())
            }
        }
        preferencias.edit().putString("contas", contas.toString()).apply()
    }

    fun pets(): MutableList<Pet> {
        val chave = "pets_$usuario"
        if (!preferencias.contains(chave)) {
            return if (usuario == "mariana.silva@exemplo.com") mutableListOf(
                Pet(1, "Pipoca", "Cão", "Golden Retriever", "2", "28", foto = "pipoca"),
                Pet(2, "Floco", "Gato", "Persa", "3", "4.5", foto = "floco")
            ) else mutableListOf()
        }
        val lista = JSONArray(preferencias.getString(chave, "[]"))
        return (0 until lista.length()).map { i ->
            val p = lista.getJSONObject(i)
            Pet(p.getLong("id"), p.getString("nome"), p.getString("especie"), p.getString("raca"),
                p.getString("idade"), p.getString("peso"), p.optString("observacoes"), p.optString("foto"))
        }.toMutableList()
    }

    fun salvarPet(pet: Pet) {
        val lista = pets()
        val indice = lista.indexOfFirst { it.id == pet.id }
        if (indice >= 0) lista[indice] = pet else lista.add(pet)
        val json = JSONArray()
        lista.forEach { p -> json.put(JSONObject().apply {
            put("id", p.id); put("nome", p.nome); put("especie", p.especie); put("raca", p.raca)
            put("idade", p.idade); put("peso", p.peso); put("observacoes", p.observacoes); put("foto", p.foto)
        }) }
        preferencias.edit().putString("pets_$usuario", json.toString()).apply()
    }

    fun compromissos(): MutableList<Compromisso> {
        val chave = "agenda_$usuario"
        if (!preferencias.contains(chave)) {
            return if (usuario == "mariana.silva@exemplo.com") mutableListOf(
                Compromisso(1, "Bruno Oliveira", "Pipoca", "Dog Walking", "26/09/2026", "16:00", true),
                Compromisso(2, "Ana Paula (Sitter)", "Floco", "Pet Sitting", "27/09/2026", "10:00")
            ) else mutableListOf()
        }
        val json = JSONArray(preferencias.getString(chave, "[]"))
        return (0 until json.length()).map { i ->
            val c = json.getJSONObject(i)
            Compromisso(c.getLong("id"), c.getString("profissional"), c.getString("pet"),
                c.getString("servico"), c.getString("data"), c.getString("horario"), c.optBoolean("confirmado"))
        }.toMutableList()
    }

    fun salvarCompromissos(lista: List<Compromisso>) {
        val json = JSONArray()
        lista.forEach { c -> json.put(JSONObject().apply {
            put("id", c.id); put("profissional", c.profissional); put("pet", c.pet)
            put("servico", c.servico); put("data", c.data); put("horario", c.horario); put("confirmado", c.confirmado)
        }) }
        preferencias.edit().putString("agenda_$usuario", json.toString()).apply()
    }

    fun mensagens(contato: String): MutableList<String> {
        val json = JSONArray(preferencias.getString("chat_${usuario}_$contato", "[]"))
        return (0 until json.length()).map { json.getString(it) }.toMutableList()
    }

    fun enviarMensagem(contato: String, texto: String) {
        val lista = mensagens(contato)
        lista.add(texto)
        preferencias.edit().putString("chat_${usuario}_$contato", JSONArray(lista).toString()).apply()
    }

    var favorito: Boolean
        get() = preferencias.getBoolean("favorito_$usuario", false)
        set(valor) { preferencias.edit().putBoolean("favorito_$usuario", valor).apply() }

    private fun criarSenha(senha: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return Base64.encodeToString(sal, Base64.NO_WRAP) + ":" + derivarSenha(senha, sal)
    }

    private fun conferirSenha(senha: String, armazenada: String): Boolean {
        val partes = armazenada.split(":")
        return partes.size == 2 && java.security.MessageDigest.isEqual(
            derivarSenha(senha, Base64.decode(partes[0], Base64.NO_WRAP)).toByteArray(), partes[1].toByteArray())
    }

    private fun derivarSenha(senha: String, sal: ByteArray): String {
        val chave = PBEKeySpec(senha.toCharArray(), sal, 120000, 256)
        return try {
            Base64.encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(chave).encoded, Base64.NO_WRAP)
        } finally { chave.clearPassword() }
    }
}
