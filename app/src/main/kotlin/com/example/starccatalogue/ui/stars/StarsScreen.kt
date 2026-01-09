package com.example.starccatalogue.ui.stars

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.ac.starlink.table.StarTable

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StarsScreen(
    viewModel: StarsViewModel = StarsViewModel(),
) {
    val stars by viewModel.stars.collectAsStateWithLifecycle()
    StarsScreen(
        stars = stars,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StarsScreen(
    stars: StarTable
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sterne")},
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stars.rowCount.toInt()) { num ->
                Card(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stars.getRow(num.toLong())[0].toString(),
                        )
                    }
                }

            }
        }
    }
}

