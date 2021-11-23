package symbol_table

class BuiltinTypeSymbol(name: String) : Symbol(name) {
    override fun toString(): String {
        return "BuiltinTypeSymbol(name='${this.name}')"
    }
}