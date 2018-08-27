package com.example.usuario.prueba;
/*# Errores relacionados con los dispositivos = Ed

# Errores relacionados con cada una de las pruebas se nombran como sigue:
# AF = Alcance funcional
# EQ = Equilibrio
# VM = Velocidad de la marcha
# IDS = Incorporarse de una silla*/

public enum ESPPB_events {
    //Funcionalidad Correcta
    OK(0,"La ejecución fue correcta"),

    //Errores en las pruebas

    //Alcance Funcional
    AF_FootRise(1,"Se ha levantado el pie"),
    AF_Arm(2, "Se ha perdido la posición del brazo"),

    //Equilibrio
    EQ_FootRise(3,"Se ha levantado el pie"),

    //Velocidad de la Marcha
    VM_OutMeasureRegion(4, ""),
    VM_WronginitialPosition(5,""),

    //Incorporarse de una Silla
    IDS_FootRise(6,"Se ha levantado el pie"),
    IDS_ArmOutPosition(7,"Se ha perdido la posición del brazo"),
    IDS_WrongChairPosition(8,"Posición incorrecta en la silla"),


    //Errores en Dispositivos

    //Módulos Velocidad de la Marcha
    VM_LowBattery(9,"Batería baja de los módulos"),
    VM_MissAlignement(10,"Se ha perdido la alineación"),
    VM_WrongDistance(11,"La distancia se ha modificado"),
    VM_LostDevice(12,"Se ha perdido comunicación de los sensores"),

    //Plataforma
    PF_LowBattery(13,"Batería baja de la Plataforma"),
    PF_CommunicationLost(14,"Se ha perdido comunicación con la Plataforma"),

    //Manilla
    M_LowBattery(15,"Batería baja de la Manilla"),
    M_CommunicationLost(16,"Se ha perdido comunicación con la Manilla"),


    //Posición de Silla
    PS_LowBattery(17,"Batería baja del Sensor de la silla"),
    PS_CommunicationLost(18,"Se ha perdido comunicación con el Sensor de la silla"),

    ;

    ESPPB_events(int i, String msg) {
        this.type = i;
        this.message = msg;
    }

    private int type;
    private String message;

    //Obtener el String del Enum
    public String getStringMessage()
    {
        return message;
    }

    //Obtener el Número del Enum
    public int getNumericType()
    {
        return type;
    }

    //With this ESPPB_Event.fromInt we can find the value from an int
    public static ESPPB_events fromInt(int i) {
        for (ESPPB_events b : ESPPB_events.values()) {
            if (b.getNumericType() == i) { return b; }
        }
        return null;
    }
}
