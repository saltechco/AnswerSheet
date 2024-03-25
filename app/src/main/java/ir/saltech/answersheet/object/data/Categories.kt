package ir.saltech.answersheet.`object`.data

import android.util.Log

class Categories : Things {
    private var categories: MutableList<Category>

    constructor() {
        categories = ArrayList()
    }

    constructor(categories: MutableList<Category>) {
        this.categories = categories
    }

    val things: List<Thing?>?
        get() = super.convertToThings(categories)

    fun getCategories(): List<Category> {
        return categories
    }

    fun setCategories(categories: MutableList<Category>) {
        this.categories = categories
    }

    fun addCategory(c: Category, position: Int) {
        categories.add(position, c)
    }

    fun addCategory(c: Category) {
        categories.add(c)
    }

    fun removeCategory(index: Int) {
        categories.removeAt(index)
    }

    fun isCategoryAvailable(c: Category): Boolean {
        var isExists = false
        for (cse in categories) {
            Log.v("TAG", "ir.saltech eee$cse")
            if (cse.name == c.name) {
                isExists = true
                break
            }
        }
        return !isExists
    }

    override fun toString(): String {
        return "Categories{" +
                "categories=" + categories +
                '}'
    }
}

