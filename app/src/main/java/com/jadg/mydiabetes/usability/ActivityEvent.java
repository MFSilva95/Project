package com.jadg.mydiabetes.usability;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.jadg.mydiabetes.database.DB_Write;

// classe que representa uma actividade em duracao
// contem o nome da atividade e marca o inicio e o fim da atividade
// no fim da atividade (funcao stop()) guarda uma entrada na base de dados
public class ActivityEvent
{
    private String evtDescription;      // descricao da actividade em duracao
    private Date startTime;             // definido na inicializacao de um ActivityEvent
    private DB_Write activityDB;        // instancia de escrita na base de dados para guardar a entrada

    // Construtor, a instancia da base de dados e a descricao da atividade
    // inicia o contador, i. e. define o startTime
    public ActivityEvent(DB_Write activityDB, String evtDescription) {
        this.activityDB = activityDB;
        this.evtDescription = evtDescription;
        this.startTime = new Date();

        // print no LOG de debug, pode ser comentado
        Log.d("ActivityEvent_Start", evtDescription);
    }

    // funcao que termina a atividade atual
    // guarda o tempo em que esta acabou e insere uma novaa
    // entrada na base de dados a representar o evento
    public void stop() {
        // print no LOG de debug, pode ser comentado
        Log.d("ActivityEvent_Stop", evtDescription);

        Date endTime = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        activityDB.newEvent(evtDescription, dateFormat.format(startTime), dateFormat.format(endTime),
                (int) (endTime.getTime() - startTime.getTime()));
    }
}
