package com.calculator.vault.lock.hide.photo.video.ui.contact

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Contact_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ItemContactBinding
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity.Companion.IsSelectAll
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity.Companion.floating_btn_contact
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity.Companion.floating_del_contact
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity.Companion.select_contact_datas
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity.Companion.selecter


class ContactAdapter(var context: Context, var activity: Activity) :
    BaseAdapter<ItemContactBinding, Contact_Data>(
        R.layout.item_contact
    ) {


    private var formatted: String? = null
    var IsLongClick = false

    override fun setClickableView(binding: ItemContactBinding): List<View?> {
        return listOf(binding.root)
    }

    override fun onBind(
        binding: ItemContactBinding,
        position: Int,
        item: Contact_Data,
        payloads: MutableList<Any>?
    ) {
        binding.run {


            adLayout.visibility = View.GONE
            adcard.visibility = View.GONE


            contactBack.setOnLongClickListener {
                if (!IsLongClick) {
                    IsLongClick = true
                    addRemoveSelectionList(binding, position, item)
                }
                true
            }

            contactBack.setOnClickListener {
                if (IsLongClick) {
                    addRemoveSelectionList(binding, position, item)
                } else {
                    val intent = Intent(activity, AddContactActivity::class.java)
                    intent.putExtra("type", 1)
                    intent.putExtra("data", item)
                    activity.startActivity(intent)
                }
            }

            contactCall.setOnClickListener(View.OnClickListener {
                if (!IsLongClick) {
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:" + item.number)
                    activity.startActivity(callIntent)
                }
            })

            contactMessage.setOnClickListener {
                if (!IsLongClick) {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data =
                        Uri.parse("smsto:" + Uri.encode(item.number))
                    activity.startActivity(intent)
                    /*if (getDefaultSmsAppPackageName(activity) != null) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
        //                        intent.putExtra("sms_body", "");
                                intent.setType("vnd.android-dir/mms-sms");
                                intent.putExtra("address", contact_datas.get(position).getNumber());
                                activity.startActivity(intent);
                            }*/
                }
            }

            contactName.text = item.name
            contactNumber.text = item.number
            if (select_contact_datas.contains(item)) {
                contactSelect.visibility = View.VISIBLE
            } else {
                contactSelect.visibility = View.GONE
            }
        }


    }

    /*public static String getDefaultSmsAppPackageName(@NonNull Context context) {
        String defaultSmsPackageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            return defaultSmsPackageName;
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                return resolveInfos.get(0).activityInfo.packageName;

        }
        return null;
    }*/
    fun addRemoveSelectionList(holder: ItemContactBinding, position: Int, item: Contact_Data) {
        try {
            holder.contactSelect.visibility = View.VISIBLE
            floating_btn_contact?.visibility = View.GONE
            floating_del_contact?.visibility = View.VISIBLE
            selecter?.isVisible = true
            if (select_contact_datas.contains(item)) {
                select_contact_datas.remove(item)
                holder.contactSelect.visibility = View.GONE
            } else {
                select_contact_datas.add(item)
                holder.contactSelect.visibility = View.VISIBLE
            }
            if (select_contact_datas.size == 0) {
                IsLongClick = false
                notifyItemChanged(position)
                floating_btn_contact?.visibility = View.VISIBLE
                floating_del_contact?.visibility = View.GONE
                selecter?.isVisible = false
                IsSelectAll = false
                selecter?.icon = activity.resources.getDrawable(R.drawable.iv_unselect)
            }
            if (select_contact_datas.size == displayList.size) {
                IsSelectAll = true
                selecter?.icon = activity.resources.getDrawable(R.drawable.iv_select)
            } else {
                IsSelectAll = false
                selecter?.icon = activity.resources.getDrawable(R.drawable.iv_unselect)
            }
        } catch (e: Exception) {
        }
    }

}
