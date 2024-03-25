package ir.saltech.answersheet.`object`.data

class CategoryColors {
    private var categoryColors: MutableList<CategoryColor>

    init {
        categoryColors = ArrayList()
    }

    fun getCategoryColors(): List<CategoryColor> {
        return categoryColors
    }

    fun setCategoryColors(categoryColors: MutableList<CategoryColor>) {
        this.categoryColors = categoryColors
    }

    fun addCategoryColor(q: CategoryColor) {
        categoryColors.add(q)
    }

    override fun toString(): String {
        return "CategoryColors{" +
                "categoryColors=" + categoryColors +
                '}'
    }
}
