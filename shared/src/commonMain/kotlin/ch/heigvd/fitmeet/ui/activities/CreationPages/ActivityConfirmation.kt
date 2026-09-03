    package ch.heigvd.fitmeet.ui.activities.CreationPages

    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.safeContentPadding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import ch.heigvd.fitmeet.data.activityCreation.EventRepository
    import ch.heigvd.fitmeet.navigation.ActivityList
    import ch.heigvd.fitmeet.navigation.MainGraph
    import ch.heigvd.fitmeet.ui.activities.activityData
    import ch.heigvd.fitmeet.ui.activities.currentScreen
    import ch.heigvd.fitmeet.ui.activities.parsePosition
    import ch.heigvd.fitmeet.ui.activities.reset
    import ch.heigvd.fitmeet.ui.components.LevelChip
    import ch.heigvd.fitmeet.ui.map.MapScreen
    import kotlinx.coroutines.launch
    import org.jetbrains.compose.resources.painterResource


    private val Navy = Color(0xFF0B2545)
    private val Green = Color(0xFF429A72)
    @Composable
    fun ActivityConfirmation(
        eventRepository: EventRepository,
        navController: NavController
    ) {
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSaving by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        MaterialTheme {
            Column(
                modifier = Modifier
                    .background(Navy)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    onClick = {
                        activityData.reset()
                        navController.navigate(ActivityList) {
                            launchSingleTop = true
                            popUpTo(MainGraph) {
                                inclusive = false
                            }
                        }
                        currentScreen.value = 0
                              },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .background(Color(0xFFFF0000))
                        .align(Alignment.Start)
                ) {
                    Text(
                        "X",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.W900,
                        fontSize = 18.sp,
                        color = Color(0xFFFFFFFF)
                    )
                }
                Spacer(
                    modifier = Modifier.height(50.dp)
                )
                Text(
                    "Résumé de l'activité : ",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.W600,
                    color = Green
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    activityData.type?.let { sport ->
                        Image(
                            painter = painterResource(sport.icon),
                            contentDescription = sport.label,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = activityData.name,
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                Text(activityData.description,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                Text(
                    text = "Le ${activityData.date} à ${activityData.time} " +
                            "pendant "+ if(activityData.duration != 0)
                        "${activityData.duration} min"
                    else
                        "une durée non spécifiée",
                    fontSize = 15.sp,
                    color = Color.White
                )
                val (lat, lng) = parsePosition(activityData.position)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(30.dp)
                ) {
                    MapScreen(
                        selectedLat = lat,
                        selectedLng = lng
                    )
                }

                //Text("Position géographique de l'activité: ${activityData.position}")
                Text("Type de l'activité: ${activityData.type?.label}",
                    fontSize = 18.sp,
                    color = Color.White
                )
                Row(){
                    Text("Difficulté de l'activité: ",
                        fontSize = 18.sp,
                        color = Color.White)
                    LevelChip(
                        level = activityData.difficulty
                    )
                }
                Spacer(
                    modifier = Modifier.weight(1f)
                )

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =    Green,
                        disabledContainerColor = Color(0xFF888888)
                    ),
                    onClick = {
                        scope.launch {
                            isSaving = true
                            eventRepository.createEvent(
                                type = activityData.type.toString(),
                                date = activityData.date,
                                time = activityData.time,
                                duration = activityData.duration,
                                position = activityData.position,
                                name = activityData.name,
                                description = activityData.description,
                                difficulty = activityData.difficulty.label,
                                capacity = activityData.capacity
                            ).onSuccess {
                                errorMessage = null
                                activityData.reset()
                                currentScreen.value = 0
                                navController.navigate(ActivityList) {
                                    launchSingleTop = true
                                    popUpTo(MainGraph) {
                                        inclusive = false
                                    }
                                }
                            }.onFailure {
                                errorMessage = it.message ?: "L'activité n'a pas pu être créée."
                            }
                            isSaving = false
                        }
                    },
                    enabled = !isSaving,
                ) {
                    Text(if (isSaving) "Création..." else "Terminer la création",
                        fontSize = 35.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                        )
                }
                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
