package com.cetin072.worknotebook

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cetin072.worknotebook.data.WorkDraft
import com.cetin072.worknotebook.data.WorkItemEntity
import com.cetin072.worknotebook.speech.VoiceInputController
import com.cetin072.worknotebook.ui.theme.WorkNotebookTheme
import com.cetin072.worknotebook.widget.WorkNotebookWidgetProvider
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialWidgetAction = intent.getStringExtra(WorkNotebookWidgetProvider.EXTRA_WIDGET_ACTION)
        enableEdgeToEdge()
        setContent {
            WorkNotebookTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: MainViewModel = viewModel()
                    WorkNotebookScreen(viewModel, initialWidgetAction)
                }
            }
        }
    }
}

@Composable
private fun WorkNotebookScreen(
    viewModel: MainViewModel,
    initialWidgetAction: String?,
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val todayItems by viewModel.todayItems.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var isListening by remember { mutableStateOf(false) }
    var voiceStatus by remember {
        mutableStateOf("말하기를 누르면 다시 누를 때까지 계속 듣습니다.")
    }
    var cameraStatus by remember { mutableStateOf<String?>(null) }
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }

    val voiceController = remember(viewModel) {
        VoiceInputController(
            context = context,
            onText = viewModel::setContent,
            onListeningChanged = { isListening = it },
            onStatus = { voiceStatus = it },
        )
    }

    DisposableEffect(voiceController) {
        onDispose { voiceController.destroy() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            voiceController.start(draft.content)
        } else {
            isListening = false
            voiceStatus = "음성 기록을 사용하려면 마이크 권한을 허용해주세요."
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        val path = pendingPhotoPath
        if (success && path != null && File(path).exists()) {
            viewModel.setPhotoPath(path)
            cameraStatus = "사진을 첨부했습니다. 필요하면 메모를 적고 저장하세요."
        } else {
            path?.let { failedPath ->
                runCatching {
                    val file = File(failedPath)
                    if (file.length() == 0L) file.delete()
                }
            }
            cameraStatus = "촬영을 취소했거나 사진을 저장하지 못했습니다."
        }
        pendingPhotoPath = null
    }

    val toggleVoice: () -> Unit = {
        focusManager.clearFocus()
        if (isListening) {
            voiceController.stop()
        } else if (!voiceController.isAvailable) {
            voiceStatus = "이 기기에서 음성인식을 사용할 수 없습니다. 키보드 음성입력을 사용해주세요."
        } else if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            voiceController.start(draft.content)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val capturePhoto: () -> Unit = {
        focusManager.clearFocus()
        val photoDir = File(context.filesDir, "photos").apply { mkdirs() }
        val photoFile = File(photoDir, "photo_${System.currentTimeMillis()}.jpg")
        pendingPhotoPath = photoFile.absolutePath
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile,
        )
        cameraStatus = "카메라를 여는 중입니다…"
        takePictureLauncher.launch(uri)
    }

    LaunchedEffect(initialWidgetAction) {
        when (initialWidgetAction) {
            WorkNotebookWidgetProvider.ACTION_RECORD -> toggleVoice()
            WorkNotebookWidgetProvider.ACTION_CAMERA -> capturePhoto()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            Column {
                Text(
                    text = "업무수첩 Beta 0.2",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "생각날 때 기록하고, 해야 할 때 다시 봅니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "개발 중인 테스트 버전입니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        item(key = "today-title") {
            SectionTitle("오늘 할 일", "${todayItems.size}건")
        }

        if (todayItems.isEmpty()) {
            item(key = "today-empty") {
                EmptyCard("오늘로 지정된 미완료 업무가 없습니다.")
            }
        } else {
            items(todayItems, key = { "today-${it.id}" }) { item ->
                WorkItemCard(
                    item = item,
                    onToggle = { viewModel.toggleCompleted(item) },
                    onEdit = { viewModel.edit(item) },
                )
            }
        }

        item(key = "entry-title") {
            SectionTitle(if (draft.editingId == null) "빠른 기록" else "업무 수정")
        }

        item(key = "entry-card") {
            EntryCard(
                draft = draft,
                message = message,
                isListening = isListening,
                voiceAvailable = voiceController.isAvailable,
                voiceStatus = voiceStatus,
                cameraStatus = cameraStatus,
                onVoiceToggle = toggleVoice,
                onCapturePhoto = capturePhoto,
                onDetachPhoto = {
                    viewModel.setPhotoPath(null)
                    cameraStatus = "사진 첨부를 해제했습니다. 원본 파일은 임의로 삭제하지 않습니다."
                },
                onTitleChange = viewModel::setTitle,
                onContentChange = viewModel::setContent,
                onDateChange = viewModel::setWorkDate,
                onTimeChange = viewModel::setWorkTime,
                onSave = {
                    focusManager.clearFocus()
                    viewModel.save()
                },
                onCancel = viewModel::cancelEditing,
            )
        }

        item(key = "all-title") {
            SectionTitle("전체 기록", "${items.size}건")
        }

        if (items.isEmpty()) {
            item(key = "all-empty") {
                EmptyCard("아직 저장된 기록이 없습니다.")
            }
        } else {
            items(items, key = { "all-${it.id}" }) { item ->
                WorkItemCard(
                    item = item,
                    onToggle = { viewModel.toggleCompleted(item) },
                    onEdit = { viewModel.edit(item) },
                )
            }
        }

        item(key = "dev-version") {
            Text(
                text = "업무수첩 Beta · 0.2.0-dev · 로컬 저장 개발판",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, count: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (count != null) {
            Text(count, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EntryCard(
    draft: WorkDraft,
    message: String?,
    isListening: Boolean,
    voiceAvailable: Boolean,
    voiceStatus: String,
    cameraStatus: String?,
    onVoiceToggle: () -> Unit,
    onCapturePhoto: () -> Unit,
    onDetachPhoto: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onDateChange: (String?) -> Unit,
    onTimeChange: (String?) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onVoiceToggle,
                modifier = Modifier.fillMaxWidth(),
                enabled = voiceAvailable,
            ) {
                Text(if (isListening) "■ 중지하기" else "🎙 말하기")
            }
            Text(
                text = if (voiceAvailable) voiceStatus else "이 기기에서는 앱 내 음성인식을 사용할 수 없습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedButton(
                onClick = onCapturePhoto,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (draft.photoPath == null) "📷 문서·사진 찍기" else "📷 사진 다시 찍기")
            }

            if (cameraStatus != null) {
                Text(
                    text = cameraStatus,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (draft.photoPath != null) {
                PhotoPreview(draft.photoPath)
                TextButton(onClick = onDetachPhoto, modifier = Modifier.fillMaxWidth()) {
                    Text("사진 첨부 해제")
                }
            }

            OutlinedTextField(
                value = draft.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("제목 (선택)") },
                supportingText = { Text("비워두면 내용 앞부분으로 자동 생성합니다.") },
                singleLine = true,
            )
            OutlinedTextField(
                value = draft.content,
                onValueChange = onContentChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("업무 내용") },
                placeholder = { Text("예: 대표님께 견적 검토 결과 보고") },
                minLines = 3,
                maxLines = 7,
            )

            DateTimeControls(
                workDate = draft.workDate,
                workTime = draft.workTime,
                onDateChange = onDateChange,
                onTimeChange = onTimeChange,
            )

            if (message != null) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = (draft.content.isNotBlank() || draft.photoPath != null) && !isListening,
            ) {
                Text(
                    when {
                        isListening -> "음성 중지 후 저장"
                        draft.editingId == null -> "저장"
                        else -> "수정 저장"
                    },
                )
            }

            if (draft.editingId != null) {
                TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                    Text("수정 취소")
                }
            }

            Text(
                text = "작성 중인 내용과 사진 참조는 자동으로 임시 보관됩니다. 기록 원본은 휴대폰 안에 저장됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DateTimeControls(
    workDate: String?,
    workTime: String?,
    onDateChange: (String?) -> Unit,
    onTimeChange: (String?) -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = {
                val base = runCatching { workDate?.let(LocalDate::parse) }.getOrNull() ?: LocalDate.now()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        onDateChange(LocalDate.of(year, month + 1, day).toString())
                    },
                    base.year,
                    base.monthValue - 1,
                    base.dayOfMonth,
                ).show()
            },
            modifier = Modifier.weight(1f),
        ) {
            Text(workDate ?: "날짜 추가")
        }

        OutlinedButton(
            onClick = {
                val base = runCatching { workTime?.let(LocalTime::parse) }.getOrNull() ?: LocalTime.now()
                TimePickerDialog(
                    context,
                    { _, hour, minute -> onTimeChange("%02d:%02d".format(hour, minute)) },
                    base.hour,
                    base.minute,
                    true,
                ).show()
            },
            modifier = Modifier.weight(1f),
        ) {
            Text(workTime ?: "시간 추가")
        }
    }

    if (workDate != null || workTime != null) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (workDate != null) {
                TextButton(onClick = { onDateChange(null) }) { Text("날짜 지우기") }
            }
            if (workTime != null) {
                TextButton(onClick = { onTimeChange(null) }) { Text("시간 지우기") }
            }
        }
    }
}

@Composable
private fun WorkItemCard(
    item: WorkItemEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = item.isCompleted, onCheckedChange = { onToggle() })
                Column(modifier = Modifier.weight(1f).padding(top = 4.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null,
                    )
                    if (item.content != item.title) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                        )
                    }
                    val whenText = listOfNotNull(item.workDate, item.workTime).joinToString(" · ")
                    if (whenText.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = whenText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }

            if (item.photoPath != null) {
                Spacer(Modifier.height(8.dp))
                PhotoPreview(item.photoPath, compact = true)
            }

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) { Text("수정") }
                TextButton(onClick = onToggle) {
                    Text(if (item.isCompleted) "미완료로" else "완료")
                }
            }
        }
    }
}

@Composable
private fun PhotoPreview(photoPath: String, compact: Boolean = false) {
    val bitmap = remember(photoPath) {
        decodeSampledBitmap(photoPath, if (compact) 900 else 1400, if (compact) 600 else 1000)
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "첨부 사진",
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 120.dp else 190.dp),
            contentScale = ContentScale.Crop,
        )
    } else {
        Text(
            text = "첨부 사진을 불러올 수 없습니다.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

private fun decodeSampledBitmap(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sampleSize = 1
    while (
        bounds.outWidth / sampleSize > reqWidth * 2 ||
        bounds.outHeight / sampleSize > reqHeight * 2
    ) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return BitmapFactory.decodeFile(path, options)
}

@Composable
private fun EmptyCard(text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
