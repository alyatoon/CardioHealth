package com.example.cardiohealth

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.health.connect.datatypes.units.BloodGlucose
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimModelActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "cardiohealth.tflite"

    private lateinit var resultText: TextView
    private lateinit var BP: EditText
    private lateinit var Cholesterol: EditText
    private lateinit var EKGresults: EditText
    private lateinit var MaxHR: EditText
    private lateinit var Exerciseangina: EditText
    private lateinit var STdepression: EditText
    private lateinit var SlopeofST : EditText
    private lateinit var Thallium : EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sim_model)

        resultText = findViewById(R.id.txtResult)
        BP = findViewById(R.id.BP)
        Cholesterol = findViewById(R.id.Cholesterol)
        EKGresults = findViewById(R.id.EKGresults)
        MaxHR = findViewById(R.id.MaxHR)
        Exerciseangina = findViewById(R.id.Exerciseangina)
        STdepression = findViewById(R.id.STdepression)
        SlopeofST = findViewById(R.id.SlopeofST)
        Thallium = findViewById(R.id.Thallium)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                BP.text.toString(),
                Cholesterol.text.toString(),
                EKGresults.text.toString(),
                MaxHR.text.toString(),
                Exerciseangina.text.toString(),
                STdepression.text.toString(),
                SlopeofST.text.toString(),
                Thallium.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Terkena Penyakit Jantung"
                }else if (result == 1){
                    resultText.text = "Tidak Terkena Penyakit Jantung"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(9)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String): Int{
        val inputVal = FloatArray(8)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
