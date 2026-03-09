package com.example.starccatalogue.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.starccatalogue.R

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onProfileClick: (Int) -> Unit = {},
    onEventClick: (EventRow) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onProfileClick = onProfileClick,
        onEventClick = onEventClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onProfileClick: (Int) -> Unit,
    onEventClick: (EventRow) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            TopStarCard(
                topStar = uiState.topStar, onProfileClick = onProfileClick
            )
            Spacer(Modifier.height(16.dp))
            uiState.blogArticle?.let { article ->
                BlogSection(article)
            }
            Spacer(Modifier.height(16.dp))
            EventsList(
                events = uiState.events, onEventClick = onEventClick
            )
        }
    }
}

@Composable
private fun TopStarCard(
    topStar: TopStar?, onProfileClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color(0xFFE6EAF1), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFF7970FF),
                    modifier = Modifier.size(40.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Name des Sterns groß oben
                Text(
                    text = topStar?.name ?: stringResource(R.string.unknown_star),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
                // "Top Stern" darunter als Untertitel
                Text(
                    text = stringResource(R.string.top_star_today),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                // Beschreibung bleibt
                Text(
                    text = topStar?.descriptionRes?.let { stringResource(it) } ?: stringResource(R.string.no_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { topStar?.id?.let { onProfileClick(it) } }) {
                        Text(stringResource(R.string.to_profile))
                    }
                }
            }
        }
    }
}

@Composable
private fun BlogSection(article: BlogArticle) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(article.date, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(
                stringResource(article.titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            article.paragraphRes.forEach { paragraphRes ->
                Text(
                    stringResource(paragraphRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF545454)
                )
            }
        }
    }
}

@Composable
private fun EventsList(
    events: List<EventRow>, onEventClick: (EventRow) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.upcoming_sky_events),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            events.forEach { event ->
                EventCard(event, onClick = { onEventClick(event) })
            }
        }
    }
}

@Composable
private fun EventCard(event: EventRow, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE6EAF1), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFF9AA0A6)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(event.titleRes),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(stringResource(event.subtitleRes), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(stringResource(event.timeRes), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    val previewState = HomeUiState(
        events = listOf(
            EventRow(
                titleRes = R.string.event_1_title,
                subtitleRes = R.string.event_1_subtitle,
                timeRes = R.string.event_1_time
            ),
            EventRow(
                titleRes = R.string.event_2_title,
                subtitleRes = R.string.event_2_subtitle,
                timeRes = R.string.event_2_time
            ),
            EventRow(
                titleRes = R.string.event_3_title,
                subtitleRes = R.string.event_3_subtitle,
                timeRes = R.string.event_3_time
            )
        ),
        blogArticle = BlogArticle(
            date = "15.07.2024",
            titleRes = R.string.blog_title,
            paragraphRes = listOf(R.string.blog_paragraph_1, R.string.blog_paragraph_2)
        ),
        topStar = TopStar(
            id = 8399845,
            name = "Sirius",
            descriptionRes = R.string.top_star_description
        )
    )
    MaterialTheme {
        HomeScreen(
            uiState = previewState,
            onProfileClick = {},
            onEventClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopStarCardPreview() {
    val topStar = TopStar(
        id = 8399845,
        name = "Sirius",
        descriptionRes = R.string.top_star_description
    )
    MaterialTheme { TopStarCard(topStar = topStar, onProfileClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun BlogSectionPreview() {
    val article = BlogArticle(
        date = "15.07.2024",
        titleRes = R.string.blog_title,
        paragraphRes = listOf(R.string.blog_paragraph_1, R.string.blog_paragraph_2)
    )
    MaterialTheme { BlogSection(article) }
}

@Preview(showBackground = true, heightDp = 420)
@Composable
private fun EventsListPreview() {
    MaterialTheme {
        EventsList(
            events = listOf(
                EventRow(
                    titleRes = R.string.event_1_title,
                    subtitleRes = R.string.event_1_subtitle,
                    timeRes = R.string.event_1_time
                ),
                EventRow(
                    titleRes = R.string.event_2_title,
                    subtitleRes = R.string.event_2_subtitle,
                    timeRes = R.string.event_2_time
                ),
                EventRow(
                    titleRes = R.string.event_3_title,
                    subtitleRes = R.string.event_3_subtitle,
                    timeRes = R.string.event_3_time
                )
            ),
            onEventClick = {}
        )
    }
}
