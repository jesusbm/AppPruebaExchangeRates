package com.pruebaexchangerates.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pruebaexchangerates.R
import com.pruebaexchangerates.databinding.FragmentMainBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class InitialFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private val sTAG = InitialFragment::class.java.simpleName

    private lateinit var mBinding: FragmentMainBinding
    private val mViewModel: InitialScreenViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurateViewModel()
        configurateButtons()
    }

    override fun onResume() {
        super.onResume()
        animatePlot()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.groupId) {
            R.id.menugroup_month -> monthSelectedFromMenu(item)
            R.id.menugroup_year -> yearSelectedFromMenu(item)
            else -> false
        }
    }

    private fun monthSelectedFromMenu(item: MenuItem): Boolean {
        val monthValue = getMonthValueFromItemId(item.itemId)
        return if (monthValue in (0..11)) {
            setMonth(monthValue)
            true
        } else {
            false
        }
    }

    private fun yearSelectedFromMenu(item: MenuItem): Boolean {
        val yearValue = item.title.toString().toIntOrNull()
        return if (null != yearValue) {
            setYear(yearValue)
            true
        } else {
            false
        }
    }

    private fun configurateButtons() {
        mBinding.btnAnio.setOnClickListener {
            showPopupYear(it)
        }
        mBinding.btnMes.setOnClickListener {
            showPopupMonth(it)
        }
        mBinding.btnConsultar.setOnClickListener {
            updateData()
        }
    }

    private fun configurateViewModel() {
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mViewModel.dataXY.observe(viewLifecycleOwner, Observer {
            mBinding.ratesPlot.setData(it.first.toList(), it.second.toList(), it.second.toList())
            animatePlot()
        })
        mViewModel.eventStartQuery.observe(viewLifecycleOwner, Observer {
            Toast.makeText(
                requireContext(),
                getString(R.string.message_download),
                Toast.LENGTH_SHORT
            ).show()
        })
        mViewModel.eventCompletedQuery.observe(viewLifecycleOwner, Observer {
            Toast.makeText(
                requireContext(),
                getString(R.string.message_completed),
                Toast.LENGTH_SHORT
            ).show()
        })
        mViewModel.eventErrorQuery.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        })
    }

    private fun animatePlot() {
        val viewGroup = mBinding.root
        if (viewGroup.viewTreeObserver.isAlive) {
            viewGroup.post { mBinding.ratesPlot.startAnimation() }

        } else {
            viewGroup.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewGroup.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewGroup.post { mBinding.ratesPlot.startAnimation() }
                }
            })
        }
    }

    private fun showPopupYear(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_year, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    private fun showPopupMonth(v: View) {
        val popup = PopupMenu(requireContext(), v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_month, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    private fun setMonth(month: Int): Boolean {
        mViewModel.setMonthSelected(month)
        return true
    }

    private fun setYear(year: Int): Boolean {
        mViewModel.setYearSelected(year)
        return true
    }

    private fun updateData() {
        mViewModel.loadInfoForSelectedMonth()
    }

    private fun getMonthValueFromItemId(@IdRes itemId: Int): Int {
        return when (itemId) {
            R.id.month00 -> 0
            R.id.month01 -> 1
            R.id.month02 -> 2
            R.id.month03 -> 3
            R.id.month04 -> 4
            R.id.month05 -> 5
            R.id.month06 -> 6
            R.id.month07 -> 7
            R.id.month08 -> 8
            R.id.month09 -> 9
            R.id.month10 -> 10
            R.id.month11 -> 11
            else -> -1
        }
    }
}
