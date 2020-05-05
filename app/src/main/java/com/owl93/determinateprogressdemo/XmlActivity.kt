package com.owl93.determinateprogressdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_xml.*
import java.lang.IllegalArgumentException

class XmlActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xml)
        val bundle = intent.extras ?: throw IllegalArgumentException("bundle passed to xml activity must not me null")
        val packageName = "com.owl93.dpb.CircularProgressView"
        val textSize = 12f
        val namespaces = bundle.getStringArray("intent_namespaces")!!
        val attribs = bundle.getStringArray("intent_attributes")!!
        val values = bundle.getStringArray("intent_values")!!
        val xmlView = XMlTextView(this, textSize, packageName, namespaces, attribs, values)
        xml_container.addView(xmlView)
        fab_share_xml.setOnClickListener{
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, xmlView.getText())
            },null))
        }
    }


}