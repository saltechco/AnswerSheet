package ir.saltech.answersheet.`object`.data

class Device {
    var id: Int = 0
    private var model: String? = null
    private var manufacturer: String? = null
    var isEvoked: Boolean = false
    var isRejected: Boolean = false
    var isAccepted: Boolean = false

    fun getModel(): String {
        return model!!
    }

    fun setModel(model: String) {
        this.model = model
    }

    fun getManufacturer(): String {
        return manufacturer!!
    }

    fun setManufacturer(manufacturer: String) {
        this.manufacturer = manufacturer
    }

    override fun toString(): String {
        return "Device{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", evoked=" + isEvoked +
                ", rejected=" + isRejected +
                ", accepted=" + isAccepted +
                '}'
    }
}
