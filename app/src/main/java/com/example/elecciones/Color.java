package com.example.elecciones;

public enum Color {
    Verde, Amarillo, Azul, Rojo;

    public int getColorInt() {
        switch (this) {
            case Verde:
                return android.graphics.Color.parseColor("#C8E6C9"); // Verde suave
            case Amarillo:
                return android.graphics.Color.parseColor("#FFF59D"); // Amarillo suave
            case Azul:
                return android.graphics.Color.parseColor("#BBDEFB"); // Azul suave
            case Rojo:
                return android.graphics.Color.parseColor("#FFCDD2"); // Rojo suave
            default:
                return android.graphics.Color.LTGRAY; // Gris claro
        }
    }
}
