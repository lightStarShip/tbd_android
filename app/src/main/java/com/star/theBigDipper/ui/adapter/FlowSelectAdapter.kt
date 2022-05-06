package com.star.theBigDipper.ui.adapter

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.star.theBigDipper.R
import com.star.theBigDipper.model.bean.FlowBean
import java.util.*

class FlowSelectAdapter(private val mContext: Context, private val mBytesPerToken: Double, rechargeFlowState: RechargeFlowState) : RecyclerView.Adapter<FlowSelectAdapter.ViewHolder>() {
    private val flows: MutableList<FlowBean> = ArrayList()
    private val mRechargeFlowState: RechargeFlowState?

    init {
        flows.add(FlowBean(500.0, 500 / mBytesPerToken, 0))
        flows.add(FlowBean(1000.0, 1000 / mBytesPerToken, 0))
        flows.add(FlowBean(2000.0, 2000 / mBytesPerToken, 0))
        flows.add(FlowBean(5000.0, 5000 / mBytesPerToken, 0))
        flows.add(FlowBean(8000.0, 8000 / mBytesPerToken, 0))
        flows.add(FlowBean(0.0, 0.0, 1))
        mRechargeFlowState = rechargeFlowState
    }

    interface RechargeFlowState {
        fun recharge(bytesInm: Double)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_flow_select, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val flowBean = flows[position]
        if (flowBean.type == 1) {
            viewHolder.customTv.visibility = View.VISIBLE
            viewHolder.flowTv.visibility = View.GONE
            viewHolder.hopTv.visibility = View.GONE
        } else {
            viewHolder.customTv.visibility = View.GONE
            viewHolder.flowTv.visibility = View.VISIBLE
            viewHolder.hopTv.visibility = View.VISIBLE
            if (flowBean.flow >= 1000) {
                viewHolder.flowTv.text = (flowBean.flow / 1000).toString() + "G"
            } else {
                viewHolder.flowTv.text = flowBean.flow.toString() + "M"
            }
            val hopText = String.format(Locale.CHINA, "%.4f ", flowBean.hop) + mContext.getString(R.string.wallet_token_unit)
            viewHolder.hopTv.text = hopText
        }
        if (flowBean.isSelected) {
            viewHolder.flowTv.setTextColor(mContext.resources.getColor(R.color.color_white))
            viewHolder.hopTv.setTextColor(mContext.resources.getColor(R.color.color_white))
            viewHolder.constraintlayout.setBackgroundResource(R.drawable.bg_item_recharge_flow_selected)
        } else {
            viewHolder.flowTv.setTextColor(mContext.resources.getColor(R.color.color_6791c8))
            viewHolder.hopTv.setTextColor(mContext.resources.getColor(R.color.color_6791c8))
            viewHolder.constraintlayout.setBackgroundResource(R.drawable.bg_item_recharge_flow_normal)
        }
        viewHolder.constraintlayout.setOnClickListener {
            for (i in flows.indices) {
                flows[i].isSelected = i == position
            }
            notifyDataSetChanged()
            if (flowBean.type == 1) {
                showCustomerBuyFlowDialog()
            } else {
                showPasswordDialog(flowBean.hop)
            }
        }
    }

    override fun getItemCount(): Int {
        return flows.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraintlayout: ConstraintLayout
        val flowTv: TextView
        val hopTv: TextView
        val customTv: TextView

        init {
            constraintlayout = itemView.findViewById(R.id.constraintlayout)
            flowTv = itemView.findViewById(R.id.flow_tv)
            hopTv = itemView.findViewById(R.id.hop_tv)
            customTv = itemView.findViewById(R.id.custom_tv)
        }
    }

    private fun showCustomerBuyFlowDialog() {
        val dialog = BottomSheetDialog(mContext, R.style.BottomSheetStyle)
        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_customer_buy_flow, null)
        val backTv = dialogView.findViewById<View>(R.id.back_tv) as TextView
        val hopNumberEt = dialogView.findViewById<View>(R.id.hop_bumber_et) as EditText
        val flowNumberEt = dialogView.findViewById<View>(R.id.flow_bumber_tv) as TextView
        val submitOrderTv = dialogView.findViewById<View>(R.id.submit_order_tv) as TextView
        val exchangePriceTv = dialogView.findViewById<View>(R.id.exchange_tv) as TextView
        val price = String.format(Locale.CHINA, "1HOP=%.2fM", mBytesPerToken)
        exchangePriceTv.text = price
        flowNumberEt.keyListener = null
        hopNumberEt.hint = mContext.getString(R.string.recharge_hop_money)
        hopNumberEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val text = hopNumberEt.text.toString()
                if (TextUtils.isEmpty(text) || text == ".") {
                    return
                }
                val flow = (hopNumberEt.text.toString().toDouble() * mBytesPerToken).toString() + "M"
                flowNumberEt.text = flow
            }
        })
        submitOrderTv.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            if (TextUtils.isEmpty(hopNumberEt.text.toString())) {
                return@OnClickListener
            }
            showPasswordDialog(hopNumberEt.text.toString().toDouble())
        })
        backTv.setOnClickListener { dialog.dismiss() }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(dialogView)
        try {
            // hack bg color of the BottomSheetDialog
            val parent = dialogView.parent as ViewGroup
            parent.setBackgroundResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dialog.show()
    }

    private fun showPasswordDialog(tokenNo: Double) {
        mRechargeFlowState?.recharge(tokenNo)
    }


}