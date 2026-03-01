package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.NightSkyBlack
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.NotebookBlue
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.TaroPurple
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.TomatoRed
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private data class ThemeOption(
    val name: String,
    val color: Color
)

private val themeOptions = listOf(
    ThemeOption("Taro Purple", TaroPurple),
    ThemeOption("NotebookBlue", NotebookBlue),
    ThemeOption("Tomato Red", TomatoRed),
    ThemeOption("Night Sky Black", NightSkyBlack)
)

data class QuoteResponse(
    @SerializedName("quote") val quote: String,
    @SerializedName("author") val author: String
)

interface QuoteApi {
    @GET("quotes/random")
    suspend fun getRandomQuote(): QuoteResponse
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: QuoteApi by lazy {
        retrofit.create(QuoteApi::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CP3406_CP5603UtilityAppStarterTemplateTheme {
                UtilityApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    CP3406_CP5603UtilityAppStarterTemplateTheme {
        UtilityApp()
    }
}

@Composable
fun UtilityApp(viewModel: NoteViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf("Home") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var selectedThemeColor by remember { mutableStateOf(NotebookBlue) }
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    var showQuoteDialog by remember { mutableStateOf(false) }
    var quoteText by remember { mutableStateOf("") }
    var quoteAuthor by remember { mutableStateOf("") }
    var isLoadingQuote by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (selectedNote != null) {
        EditNoteScreen(
            note = selectedNote!!,
            onBack = { updatedTitle, updatedContent ->
                viewModel.updateNote(selectedNote!!.copy(title = updatedTitle, content = updatedContent))
                selectedNote = null
            },
            themeColor = selectedThemeColor,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    } else {
        Scaffold(
            topBar = {
                Surface(
                    color = selectedThemeColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notebook",
                        color = Color.White,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp).padding(top = 24.dp)
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == "Home") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FloatingActionButton(
                            onClick = {
                                isLoadingQuote = true
                                scope.launch {
                                    try {
                                        val quote = withContext(Dispatchers.IO) {
                                            RetrofitInstance.api.getRandomQuote()
                                        }
                                        quoteText = quote.quote
                                        quoteAuthor = quote.author
                                        showQuoteDialog = true
                                    } catch (e: Exception) {
                                        quoteText = "Failed to load quote"
                                        quoteAuthor = ""
                                        showQuoteDialog = true
                                    } finally {
                                        isLoadingQuote = false
                                    }
                                }
                            },
                            containerColor = selectedThemeColor,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.padding(bottom = 16.dp, start = 45.dp)
                        ) {
                            if (isLoadingQuote) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Q", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        FloatingActionButton(
                            onClick = {
                                viewModel.addNote("<New Note>", "")
                            },
                            containerColor = selectedThemeColor,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add note")
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home page") },
                        label = { Text("Home") },
                        selected = selectedTab == "Home",
                        onClick = { selectedTab = "Home" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings page") },
                        label = { Text("Settings") },
                        selected = selectedTab == "Settings",
                        onClick = { selectedTab = "Settings" }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Home" -> HomeScreen(
                        notes = notes,
                        onDeleteNote = { viewModel.deleteNote(it) },
                        onNoteClick = { selectedNote = it }
                    )
                    "Settings" -> SettingsScreen(
                        selectedThemeColor = selectedThemeColor,
                        onThemeColorSelected = { selectedThemeColor = it }
                    )
                }
            }
        }

        if (showQuoteDialog) {
            AlertDialog(
                onDismissRequest = { showQuoteDialog = false },
                title = { Text("Famous Quote") },
                text = {
                    Column {
                        Text(
                            text = "\"$quoteText\"",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (quoteAuthor.isNotEmpty()) {
                            Text(
                                text = "- $quoteAuthor",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("quote", "$quoteText - $quoteAuthor")
                            clipboard.setPrimaryClip(clip)
                            showQuoteDialog = false
                        }
                    ) {
                        Text("Copy")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuoteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(notes: List<Note>, onDeleteNote: (Note) -> Unit, onNoteClick: (Note) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
        //.padding(16.dp)
    ) {
        if (notes.isEmpty()) {
            Text(
                text = "Tap the \"+\" icon in the bottom right corner to create a new note",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                ),
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column {
                Text(
                    text = "Swipe the note entry to the left to delete the note",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        lineHeight = 32.sp
                    ),
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(thickness = 1.dp, color = Color.Black)
                //Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(notes, key = { it.id }) { note ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    onDeleteNote(note)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Icon",
                                        tint = Color.White
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false,
                            content = {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNoteClick(note) },
                                    color = Color(0xFFF5F5F5)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        )
                                        Text(
                                            text = note.content.lines().getOrNull(1) ?: "",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        )
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun EditNoteScreen(
    note: Note,
    onBack: (String, String) -> Unit,
    themeColor: Color,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    var content by remember { mutableStateOf(note.content) }

    Scaffold(
        topBar = {
            Surface(
                color = themeColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Notebook",
                    color = Color.White,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp).padding(top = 24.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val lines = content.lines()
                    val updatedTitle = if (lines.isNotEmpty() && lines[0].isNotBlank()) {
                        lines[0]
                    } else {
                        note.title
                    }
                    onBack(updatedTitle, content)
                },
                containerColor = themeColor,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home page") },
                    label = { Text("Home") },
                    selected = selectedTab == "Home",
                    onClick = { onTabSelected("Home") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings page") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { onTabSelected("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val lines = content.lines()
            val title = if (lines.isNotEmpty()) lines[0] else ""

            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxSize(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text = "Start to edit...",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun SettingsScreen(
    selectedThemeColor: Color,
    onThemeColorSelected: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Settings Screen", style = MaterialTheme.typography.headlineMedium)
        Text("The settings page allows adjustment of theme colors")
        Spacer(modifier = Modifier.height(28.dp))
        Text("Theme Color", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            themeOptions.forEach { option ->
                val isSelected = selectedThemeColor == option.color
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onThemeColorSelected(option.color) }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .background(option.color, CircleShape)
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = option.name,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
