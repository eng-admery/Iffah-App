package com.example.iffah.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iffah.IffahViewModel
import com.example.iffah.data.RelapseEntity
import com.example.iffah.ui.theme.DangerRed
import com.example.iffah.ui.theme.LightGreenBg
import com.example.iffah.ui.theme.PrimaryGreen
import com.example.iffah.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: IffahViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val relapses by viewModel.relapsesList.collectAsState()
    val mostCommon = viewModel.getMostCommonTrigger()
    val dateFormat = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar"))

    Scaffold(
        containerColor = LightGreenBg,
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("سجل الانتكاسات", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← رجوع", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "⚠️ انتبه لنقطة الضعف!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "أكثر محفز يؤدي للانتكاسة لديك هو:", fontSize = 14.sp, color = Color.Gray)
                    Text(text = mostCommon, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = WarningOrange)
                }
            }

            Text(
                text = "السجل الكامل:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PrimaryGreen,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (relapses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "لا يوجد سجل انتكاسات بعد..\nأحمد الله وحافظ على عافيتك!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(relapses) { relapse ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = relapse.trigger,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = DangerRed
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = dateFormat.format(Date(relapse.timestamp)),
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(text = "❌", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}