package symbol_table

open class Symbol(val name: String, val type: Symbol? = null) {
    override fun toString(): String {
        return "Symbol(name='${this.name}', type=${this.type})"
    }
}
