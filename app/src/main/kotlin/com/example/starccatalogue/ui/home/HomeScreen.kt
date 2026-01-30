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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onProfileClick: (Int) -> Unit = {},
    onEventClick: (EventRow) -> Unit = {},
    onSearch: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onProfileClick = onProfileClick,
        onEventClick = onEventClick,
        onSearch = onSearch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onProfileClick: (Int) -> Unit,
    onEventClick: (EventRow) -> Unit,
    onSearch: (String) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SearchBar(onSearch = onSearch)
            Spacer(Modifier.height(16.dp))
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
private fun SearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Sterne suchen...",
    onSearch: (String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        color = Color(0xFFE6EAF1),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(all = 4.dp)
            )

            BasicTextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            ) { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }
                innerTextField()
            }
            IconButton(onClick = { onSearch(value) }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Suchen")
            }
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
                    text = topStar?.name ?: "Unbekannter Stern",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
                // "Top Stern" darunter als Untertitel
                Text(
                    text = "Heutiger Top Stern",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                // Beschreibung bleibt
                Text(
                    text = topStar?.description ?: "Keine Beschreibung vorhanden.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { topStar?.id?.let { onProfileClick(it) } }) {
                        Text("zum Profil")
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
                article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            article.paragraphs.forEach { paragraph ->
                Text(
                    paragraph,
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
            "Nächste Himmelevents",
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
                    event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(event.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(event.time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
                "Sonnenfinsternis",
                "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                "Taggesamtzeit - 10:00"
            ), EventRow(
                "Mars und Saturn stehen im Zwiespalt",
                "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                "Stundenhalbzeit - 10:30"
            ), EventRow(
                "Pluto wird wieder Planet",
                "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                "Normalzeit - 13:37"
            )
        ), blogArticle = BlogArticle(
            date = "15.07.2024", title = "Aktuelle Beobachtungen", paragraphs = listOf(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
            )
        ), topStar = TopStar(
            id = 8399845, name = "Sirius", description = "so schön ja"
        )
    )
    MaterialTheme {
        HomeScreen(
            uiState = previewState,
            onProfileClick = {},
            onEventClick = {},
            onSearch = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(Color.White)) {
            SearchBar(modifier = Modifier.padding(16.dp), onSearch = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopStarCardPreview() {
    val topStar = TopStar(
        id = 8399845, name = "Sirius", description = "so schön ja"
    )
    MaterialTheme { TopStarCard(topStar = topStar, onProfileClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun BlogSectionPreview() {
    val article = BlogArticle(
        date = "15.07.2024", title = "Aktuelle Beobachtungen", paragraphs = listOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
        )
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
                    "Sonnenfinsternis",
                    "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                    "Taggesamtzeit - 10:00"
                ), EventRow(
                    "Mars und Saturn stehen im Zwiespalt",
                    "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                    "Stundenhalbzeit - 10:30"
                ), EventRow(
                    "Pluto wird wieder Planet",
                    "Description duis aute irure dolor in reprehenderit in voluptate velit.",
                    "Normalzeit - 13:37"
                )
            ), onEventClick = {})
    }
}
