package com.thomaskioko.paybillmanager.mobile.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import com.thomaskioko.paybillmanager.domain.model.Category
import com.thomaskioko.paybillmanager.mobile.R
import com.thomaskioko.paybillmanager.mobile.extension.hide
import com.thomaskioko.paybillmanager.mobile.extension.show
import com.thomaskioko.paybillmanager.mobile.injection.Injectable
import com.thomaskioko.paybillmanager.mobile.mapper.CategoryViewMapper
import com.thomaskioko.paybillmanager.mobile.ui.NavigationController
import com.thomaskioko.paybillmanager.mobile.ui.adapter.CategoriesAdapter
import com.thomaskioko.paybillmanager.mobile.util.NumberFormatter
import com.thomaskioko.paybillmanager.presentation.model.CategoryView
import com.thomaskioko.paybillmanager.presentation.state.Resource
import com.thomaskioko.paybillmanager.presentation.state.ResourceState
import com.thomaskioko.paybillmanager.presentation.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_bill_amount.*
import timber.log.Timber
import javax.inject.Inject


class BillAmountFragment : Fragment(), Injectable,
        CategoriesAdapter.OnRecyclerViewItemClickListener, Step {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var mapper: CategoryViewMapper
    @Inject
    lateinit var sharedViewModel: SharedViewModel
    @Inject
    lateinit var navigationController: NavigationController

    private lateinit var categoriesAdapter: CategoriesAdapter
    private var categoryId: String = ""
    private var stringBuilder = StringBuilder()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_amount, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                .get(SharedViewModel::class.java)

        sharedViewModel.getAmount().observe(this, Observer {
            tv_bill_amount.text = NumberFormatter.formatNumber(it)
        })

        btn_delete.isEnabled = false


        setUpRecyclerView()

        tv_t9_key_0.setOnClickListener{
            setAmountText("0")
        }
        tv_t9_key_1.setOnClickListener{
            setAmountText("1")
        }
        tv_t9_key_2.setOnClickListener{
            setAmountText("2")
        }
        tv_t9_key_3.setOnClickListener{
            setAmountText("3")
        }
        tv_t9_key_4.setOnClickListener{
            setAmountText("4")
        }
        tv_t9_key_5.setOnClickListener{
            setAmountText("5")
        }
        tv_t9_key_6.setOnClickListener{
            setAmountText("6")
        }
        tv_t9_key_7.setOnClickListener{
            setAmountText("7")
        }
        tv_t9_key_8.setOnClickListener{
            setAmountText("8")
        }
        tv_t9_key_9.setOnClickListener{
            setAmountText("9")
        }

        tv_bill_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    tv_bill_amount.text = resources.getString(R.string.keyboard_0)
                    tv_bill_amount.isEnabled = false
                    btn_delete.isEnabled = false
                    btn_delete.setImageDrawable(
                            ContextCompat.getDrawable(activity!!, R.drawable.ic_backspace_disabled)
                    )
                } else {
                    btn_delete.isEnabled = true
                    tv_bill_amount.isEnabled = true
                    btn_delete.setImageDrawable(
                            ContextCompat.getDrawable(activity!!, R.drawable.ic_backspace_enabled)
                    )
                }

            }
        })

        btn_delete.setOnClickListener {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.delete(stringBuilder.length - 1, stringBuilder.length)
                tv_bill_amount.text = stringBuilder.toString()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.getCategories().observe(this,
                Observer<Resource<List<CategoryView>>> { it ->
                    it?.let { handleCategoriesData(it) }
                })

        sharedViewModel.fetchCategories()
    }

    override fun onSelected() {

    }

    override fun verifyStep(): VerificationError? {

        return if (!categoryId.isEmpty()) {
            sharedViewModel.setAmount(stringBuilder.toString())
            sharedViewModel.setCategoryId(categoryId)
            null
        } else {
            tv_error.show()
            VerificationError(resources.getString(R.string.error_category_not_selected))
        }
    }

    override fun onError(error: VerificationError) {
        Timber.e("onError! -> ${error.errorMessage}")
    }

    private fun setAmountText(amount: String) {
        stringBuilder.append(amount)
        tv_bill_amount.text = NumberFormatter.formatNumber(stringBuilder.toString())

    }

    private fun setUpRecyclerView() {
        categoriesAdapter = CategoriesAdapter(this)
        recycler_view_categories.layoutManager = LinearLayoutManager(
                activity, RecyclerView.HORIZONTAL, false
        )
        recycler_view_categories.adapter = categoriesAdapter
        categoriesAdapter.notifyDataSetChanged()

    }

    override fun selectedCategoryItem(category: Category) {
        if (tv_error.visibility == View.VISIBLE) {
            tv_error.hide()
        }
        categoryId = category.id
    }

    private fun handleCategoriesData(listResource: Resource<List<CategoryView>>) {
        when (listResource.status) {
            ResourceState.SUCCESS -> {
                updateCategories(listResource.data?.map {
                    mapper.mapToView(it)
                })
            }
            ResourceState.LOADING -> {
                recycler_view_categories.hide()
            }

            ResourceState.ERROR -> {
                Timber.e(listResource.message)
            }
        }
    }

    private fun updateCategories(list: List<Category>?) {
        recycler_view_categories.show()
        if (!list!!.isEmpty()) {
            categoriesAdapter.categoriesList = list
            categoriesAdapter.notifyDataSetChanged()
        }
    }


}

