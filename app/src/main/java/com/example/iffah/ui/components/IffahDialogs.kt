package com.example.iffah.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.ui.theme.DangerRed
import com.example.iffah.ui.theme.PrimaryGreen

@Composable
fun EmergencyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(text = "اللهم احفظني", fontWeight = FontWeight.Bold, color = PrimaryGreen)
        },
        text = {
            Column {
                Text(
                    text = "﴿ وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا ﴾",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "تنفس بعمق...\nخذ نفساً عميقاً من أنفك (4 ثوان)\nاحبسه (4 ثوان)\nأخرجه من فمك ببطء (6 ثوان)",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(text = "لقد هدأت، الحمد لله")
            }
        }
    )
}

@Composable
fun RelapseDialog(onDismiss: () -> Unit, onConfirmRelapse: (String) -> Unit) {
    val triggers = listOf("الملل", "التوتر والضغط النفسي", "السهر والوحدة", "التصفح العشوائي بدون هدف")
    var selectedTrigger by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(text = "لا تيأس، رحمة الله واسعة", fontWeight = FontWeight.Bold, color = DangerRed)
        },
        text = {
            Column {
                Text(text = "ما الذي شعرت به قبل الانتكاسة؟", fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                triggers.forEach { trigger ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTrigger == trigger,
                            onClick = { selectedTrigger = trigger }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = trigger, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTrigger.isNotEmpty()) {
                        onConfirmRelapse(selectedTrigger)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                enabled = selectedTrigger.isNotEmpty()
            ) {
                Text(text = "تسجيل")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "إلغاء", color = androidx.compose.ui.graphics.Color.Gray)
            }
        }
    )
}