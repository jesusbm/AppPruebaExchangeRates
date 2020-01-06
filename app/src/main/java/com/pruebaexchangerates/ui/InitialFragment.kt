package com.pruebaexchangerates.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.pruebaexchangerates.R
import com.pruebaexchangerates.databinding.FragmentMainBinding
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class InitialFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private val sTAG = InitialFragment::class.java.simpleName

    private lateinit var mBinding: FragmentMainBinding
    private val mViewModel: InitialScreenViewModel by viewModel()

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
        return when (item.itemId) {
            R.id.year1999 -> setYear(1999)
            R.id.year2000 -> setYear(2000)
            R.id.year2001 -> setYear(2001)
            R.id.year2002 -> setYear(2002)
            R.id.year2003 -> setYear(2003)
            R.id.year2004 -> setYear(2004)
            R.id.year2005 -> setYear(2005)
            R.id.year2006 -> setYear(2006)
            R.id.year2007 -> setYear(2007)
            R.id.year2008 -> setYear(2008)
            R.id.year2009 -> setYear(2009)
            R.id.year2010 -> setYear(2010)
            R.id.year2011 -> setYear(2011)
            R.id.year2012 -> setYear(2012)
            R.id.year2013 -> setYear(2013)
            R.id.year2014 -> setYear(2014)
            R.id.year2015 -> setYear(2015)
            R.id.year2016 -> setYear(2016)
            R.id.year2017 -> setYear(2017)
            R.id.year2018 -> setYear(2018)
            R.id.year2019 -> setYear(2019)
            R.id.year2020 -> setYear(2020)
            R.id.month00 -> setMonth(0)
            R.id.month01 -> setMonth(1)
            R.id.month02 -> setMonth(2)
            R.id.month03 -> setMonth(3)
            R.id.month04 -> setMonth(4)
            R.id.month05 -> setMonth(5)
            R.id.month06 -> setMonth(6)
            R.id.month07 -> setMonth(7)
            R.id.month08 -> setMonth(8)
            R.id.month09 -> setMonth(9)
            R.id.month10 -> setMonth(10)
            R.id.month11 -> setMonth(11)
            else -> false
        }
    }

    private fun configurateButtons() {
        mBinding.btnAnio.setOnClickListener {
            showPopupYear(it)
        }
        mBinding.btnMes.setOnClickListener {
            showPopupMonth(it)
        }
        mBinding.btnRecargar.setOnClickListener {
            mViewModel.loadInfoForSelectedMonth()
            /*val datosX = (1..31).toList()
            val datosY = datosX.map { Math.random() * 5 + it }
            mBinding.ratesPlot.setData(datosX, datosY)
            animatePlot()*/
        }
    }

    private fun configurateViewModel() {
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
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
}
