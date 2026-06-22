package com.alexkolydas.mygarage.feature.detail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.alexkolydas.mygarage.core.ui.components.*
import com.alexkolydas.mygarage.core.ui.model.ServiceUi
import com.alexkolydas.mygarage.core.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    vehicleId: Long,
    onBack: () -> Unit,
    vm: DetailViewModel = koinViewModel(parameters = { parametersOf(vehicleId) }),
) {
    val state   = vm.state.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(vm) {
        vm.effects.collect { effect ->
            when (effect) {
                DetailContract.Effect.NavigateBack -> onBack()
            }
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { u ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    u, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            vm.onIntent(DetailContract.Intent.UpdatePhoto(u.toString()))
        }
    }

    Box(Modifier.fillMaxSize().background(GarageBackground)) {
        if (state.isLoading || state.detail == null) {
            CircularProgressIndicator(
                color = GarageAccent,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            val d = state.detail
            Column(Modifier.fillMaxSize()) {

                // ── App bar ──────────────────────────────────────────────────────────
                Row(
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp),
                ) {
                    IconButton(
                        onClick = { vm.onIntent(DetailContract.Intent.BackClicked) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(GarageSurface)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(13.dp)),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GarageTextPrimary,
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = d.vehicle.model,
                            fontFamily = IbmPlexSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.2).sp,
                            color = GarageTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${d.vehicle.name} · ${d.vehicle.year}",
                            fontSize = 13.sp,
                            color = GarageTextSecond,
                        )
                    }
                }

                // ── Scrollable content ───────────────────────────────────────────────
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 18.dp, end = 18.dp, bottom = 110.dp,
                    ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Hero image
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(194.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(GarageSurface)
                                .clickable {
                                    photoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                        ) {
                            if (d.vehicle.photoUri != null) {
                                AsyncImage(
                                    model          = Uri.parse(d.vehicle.photoUri),
                                    contentDescription = null,
                                    contentScale   = ContentScale.Crop,
                                    modifier       = Modifier.fillMaxSize(),
                                )
                            } else {
                                Box(
                                    Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "Drop a photo of this bike",
                                        color = GarageTextTertiary,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                            // Gradient overlay with odometer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .background(
                                        Brush.verticalGradient(
                                            0f to Color.Transparent,
                                            0.45f to Color(0x8C0F1113),
                                            1f to Color(0xEB0F1113),
                                        )
                                    )
                                    .padding(16.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment    = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(
                                            text = "ODOMETER",
                                            fontFamily = IbmPlexMonoFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.6.sp,
                                            color = GarageTextSecond,
                                        )
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(top = 2.dp),
                                        ) {
                                            Text(
                                                text = d.vehicle.kmFormatted,
                                                fontFamily = IbmPlexMonoFamily,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 34.sp,
                                                letterSpacing = (-0.7).sp,
                                                color = GarageTextPrimary,
                                            )
                                            Text(
                                                text = "km",
                                                fontSize = 14.sp,
                                                color = GarageTextMid,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(bottom = 4.dp),
                                            )
                                        }
                                    }
                                    UrgencyChip(d.vehicle.urgency)
                                }
                            }
                        }
                    }

                    // Status strip
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            StatusTile(
                                label    = "NEXT SERVICE",
                                value    = d.nextFormatted,
                                unit     = "km",
                                modifier = Modifier.weight(1f),
                            )
                            StatusTile(
                                label    = "STATUS",
                                value    = d.statusText,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // Section header
                    item {
                        Row(
                            verticalAlignment    = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(
                                top = 26.dp, bottom = 14.dp, start = 2.dp, end = 2.dp,
                            ),
                        ) {
                            Text(
                                text = "Service history",
                                fontFamily = IbmPlexSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp,
                                color = GarageTextPrimary,
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(GarageSurface)
                                    .border(
                                        1.dp, Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(999.dp),
                                    )
                                    .padding(horizontal = 9.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${d.services.size}",
                                    fontFamily = IbmPlexMonoFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = GarageTextSecond,
                                )
                            }
                        }
                    }

                    // Service timeline
                    if (d.services.isEmpty()) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillParentMaxWidth().padding(30.dp),
                            ) {
                                Text(
                                    "No services logged yet",
                                    color = GarageTextSecond,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                )
                                Text(
                                    "Tap \"Log a service\" below to add the first record.",
                                    color = GarageTextTertiary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 5.dp),
                                )
                            }
                        }
                    } else {
                        itemsIndexed(d.services, key = { _, s -> s.id }) { index, svc ->
                            ServiceTimelineItem(
                                svc    = svc,
                                isLast = index == d.services.lastIndex,
                            )
                        }
                    }
                }
            }

            // ── Sticky "Log a service" button ────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.4f to GarageBackground,
                        )
                    )
                    .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 22.dp),
            ) {
                Button(
                    onClick = { vm.onIntent(DetailContract.Intent.LogServiceClicked) },
                    shape   = RoundedCornerShape(16.dp),
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = GarageAccent,
                        contentColor   = GarageBackground,
                    ),
                    modifier  = Modifier.fillMaxWidth().height(54.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(9.dp))
                    Text("Log a service", fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
                }
            }
        }
    }

    // Service sheet
    if (state.showServiceSheet) {
        AddServiceSheet(state = state, onIntent = vm::onIntent)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Service timeline item
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ServiceTimelineItem(svc: ServiceUi, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(14.dp),
        ) {
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(GarageBackground)
                    .border(2.dp, GarageAccent, CircleShape),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color.White.copy(alpha = 0.09f)),
                )
            }
        }

        Surface(
            shape  = RoundedCornerShape(15.dp),
            color  = GarageSurface,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 14.dp),
        ) {
            Column(Modifier.padding(horizontal = 15.dp, vertical = 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = svc.kmFormatted,
                            fontFamily = IbmPlexMonoFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = GarageTextPrimary,
                        )
                        Text(
                            text = "km",
                            fontSize = 11.5.sp,
                            color = GarageTextSecond,
                            modifier = Modifier.padding(bottom = 1.dp),
                        )
                    }
                    ServiceStatusChip(svc.urgency)
                }

                Text(
                    text = svc.work,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    color = GarageTextMid,
                    modifier = Modifier.padding(top = 9.dp),
                )

                Spacer(Modifier.height(11.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.White.copy(alpha = 0.07f))
                Spacer(Modifier.height(11.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = GarageAccent,
                        modifier = Modifier.size(15.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Next service at", fontSize = 12.5.sp, color = GarageTextSecond)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${svc.nextFormatted} km",
                        fontFamily = IbmPlexMonoFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = GarageTextPrimary,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Status tile
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatusTile(
    label: String,
    value: String,
    unit: String? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = GarageSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
        modifier = modifier,
    ) {
        Column(Modifier.padding(horizontal = 15.dp, vertical = 13.dp)) {
            Text(
                text = label,
                fontFamily = IbmPlexMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                color = GarageTextSecond,
            )
            Spacer(Modifier.height(5.dp))
            if (unit != null) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = value,
                        fontFamily = IbmPlexMonoFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = GarageTextPrimary,
                    )
                    Text(unit, fontSize = 12.sp, color = GarageTextSecond,
                        modifier = Modifier.padding(bottom = 2.dp))
                }
            } else {
                Text(
                    text = value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = GarageTextPrimary,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Add service sheet (pre-filled for this vehicle)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddServiceSheet(
    state: DetailContract.State,
    onIntent: (DetailContract.Intent) -> Unit,
) {
    val form = state.serviceForm
    ModalBottomSheet(
        onDismissRequest = { onIntent(DetailContract.Intent.CloseServiceSheet) },
        containerColor   = GarageSurfaceSheet,
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp)
                .padding(bottom = 26.dp),
        ) {
            Text("Log a service", fontWeight = FontWeight.Bold, fontSize = 22.sp,
                color = GarageTextPrimary)
            Text("Record maintenance and schedule the next one.",
                color = GarageTextSecond, fontSize = 13.sp,
                modifier = Modifier.padding(top = 3.dp, bottom = 20.dp))

            SheetLabel("ODOMETER (KM)")
            GarageInput(form.km, { onIntent(DetailContract.Intent.UpdateServiceKm(it)) },
                "e.g. 18400", mono = true)
            Spacer(Modifier.height(15.dp))

            SheetLabel("WHAT WAS DONE")
            GarageMultilineInput(form.work, { onIntent(DetailContract.Intent.UpdateServiceWork(it)) },
                "e.g. Engine oil & filter change, chain clean and lube.")
            Spacer(Modifier.height(15.dp))

            SheetLabel("NEXT SERVICE AT (KM)")
            GarageInput(form.nextKm, { onIntent(DetailContract.Intent.UpdateServiceNextKm(it)) },
                "e.g. 24000", mono = true)
            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onIntent(DetailContract.Intent.CloseServiceSheet) },
                    shape   = RoundedCornerShape(14.dp),
                    border  = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    modifier = Modifier.height(52.dp),
                ) {
                    Text("Cancel", color = GarageTextMid, fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick  = { onIntent(DetailContract.Intent.SaveService) },
                    enabled  = form.isValid,
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = GarageAccent, contentColor = GarageBackground,
                        disabledContainerColor = Color(0xFF2A323A),
                        disabledContentColor   = GarageTextTertiary,
                    ),
                    modifier = Modifier.weight(1f).height(52.dp),
                ) {
                    Text("Save service", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
