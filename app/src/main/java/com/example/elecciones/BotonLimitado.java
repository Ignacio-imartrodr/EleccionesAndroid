package com.example.elecciones;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BotonLimitado#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Ignacio
 */
public class BotonLimitado extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TEXTO = "texto";
    private static final String ARG_MAX_CLICKS = "maxClicks";
    private static final String ARG_MOSTRAR_RESTANTES = "mostrarRestantes";
    private static final String ARG_DECREMENTAL = "decremental";
    private static final String ARG_SPECIAL_CLICKS = "specialClicks";

    // TODO: Rename and change types of parameters
    private String texto;
    private int maxClicks;
    int[] specialClicks;
    private boolean mostrarRestantes;
    private boolean decremental;
    private int numClicks;
    private Button boton;
    private ListenerBtnLim listener;

    /**
     *
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param texto Texto a mostrar en el botón
     * @param maxClicks Número máximo de clicks permitidos
     * @param mostrarRestantes Decide si mostrar el número de clicks restantes en el botón
     * @param decremental Decide si el botón muestra el número de clicks restantes {@code true} o el número de clicks realizados {@code false}
     * @param specialClicks Números que ejecutaran un click especial cuando se hayan realizado esa cantidad de clicks
     * @return A new instance of fragment BotonLimitado.
     */
    // TODO: Rename and change types and number of parameters
    public static BotonLimitado newInstance(String texto, int maxClicks, boolean mostrarRestantes, boolean decremental, int[] specialClicks) {
        if (maxClicks < 0)
            throw new IllegalArgumentException("La cantidad máxima de clicks no puede ser negativa");
        BotonLimitado fragment = new BotonLimitado();
        Bundle args = new Bundle();
        args.putString(ARG_TEXTO, texto);
        args.putInt(ARG_MAX_CLICKS, maxClicks);
        args.putBoolean(ARG_MOSTRAR_RESTANTES, mostrarRestantes);
        args.putBoolean(ARG_DECREMENTAL, decremental);
        args.putIntArray(ARG_SPECIAL_CLICKS, specialClicks);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Para explicación de su funcionamiento ver {@link #newInstance(String, int, boolean, boolean, int[])}
     *
     * @return A new instance of fragment BotonLimitado.
     */
    public static BotonLimitado newInstance(String texto, int maxClicks, boolean mostrarRestantes, int[] specialClicks) {
        return BotonLimitado.newInstance(texto, maxClicks, mostrarRestantes, true, specialClicks);
    }
    /**
     * Para explicación de funcionamiento ver {@link #newInstance(String, int, boolean, boolean, int[])}
     *
     * @return A new instance of fragment BotonLimitado.
     */
    public static BotonLimitado newInstance(String texto, int maxClicks, int[] specialClicks) {
        return BotonLimitado.newInstance(texto, maxClicks, false, specialClicks);
    }
    /**
     * Para explicación de funcionamiento ver {@link #newInstance(String, int, boolean, boolean, int[])}
     *
     * @return A new instance of fragment BotonLimitado.
     */
    public static BotonLimitado newInstance(String texto, int maxClicks, boolean mostrarRestantes) {
        return BotonLimitado.newInstance(texto, maxClicks, mostrarRestantes, null);
    }
    /**
     * Para explicación de funcionamiento ver {@link #newInstance(String, int, boolean, boolean, int[])}
     *
     * @return A new instance of fragment BotonLimitado.
     */
    public static BotonLimitado newInstance(String texto, int maxClicks) {
        return BotonLimitado.newInstance(texto, maxClicks, true);
    }

    public BotonLimitado() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            texto = getArguments().getString(ARG_TEXTO, "");
            maxClicks = getArguments().getInt(ARG_MAX_CLICKS);
            mostrarRestantes = getArguments().getBoolean(ARG_MOSTRAR_RESTANTES);
            decremental = getArguments().getBoolean(ARG_DECREMENTAL);
            specialClicks = getArguments().getIntArray(ARG_SPECIAL_CLICKS);
        }
        numClicks = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_boton_limitado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtener la referencia del botón definido en el XML
        boton = view.findViewById(R.id.button);
        actualizarTexto();
        boton.setOnClickListener(this::actiOnClick);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (listener == null && context instanceof ListenerBtnLim) {
            setListener((ListenerBtnLim) context);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarTexto();
    }

    //Getters y Setters
    public void setListener(ListenerBtnLim listener){
        this.listener = listener;
    }
    public ListenerBtnLim getListener() { return listener; }

    public void setConfig(String texto, int maxClicks, boolean mostrarRestantes) {
        this.texto = texto;
        this.maxClicks = maxClicks;
        this.mostrarRestantes = mostrarRestantes;
        actualizarTexto();
    }

    public void setConfig(String texto, int maxClicks, boolean mostrarRestantes, boolean decremental, int[] specialClicks) {
        this.texto = texto;
        this.maxClicks = maxClicks;
        this.mostrarRestantes = mostrarRestantes;
        this.decremental = decremental;
        this.specialClicks = specialClicks;
        actualizarTexto();
    }

    //Métodos
    public void resetClicks(){
        numClicks = 0;
        actualizarTexto();
    }
    public void addMaxClicks(int cant) {
        maxClicks += cant;
        actualizarTexto();
    }
    private void actiOnClick(View v){
        if (listener == null) return;
        if (listener.isValidClick(this)){
            numClicks++;

            listener.onValidClick(this);

            if (numClicks == 1){
                listener.onFirstClick(this);
            }

            if (specialClicks != null){
                for (int i = 0; i < specialClicks.length && i <= maxClicks ; i++) {
                    if (numClicks == specialClicks[i]) {
                        listener.onSpecialClick(this);
                        break;
                    }
                }
            }

            listener.onNumClick(this, numClicks);

            if (numClicks == maxClicks){
                listener.onLastClick(this);
            }

            actualizarTexto();
        } else {
            listener.onInvalidClick(this);
        }
    }
    @SuppressLint("DefaultLocale")
    private void actualizarTexto(){
        if (boton == null) return;
        String texto = this.texto != null ? this.texto : "";
        if (mostrarRestantes && maxClicks > 0){
            texto = String.format("%s %d/%d", texto, decremental ? maxClicks - numClicks : numClicks, maxClicks);
        }
        boton.setText(texto);
        boton.setEnabled(maxClicks > 0 && numClicks < maxClicks);
    }
    public interface ListenerBtnLim {
        boolean isValidClick(BotonLimitado fragment);
        void onValidClick(BotonLimitado fragment);
        default void onInvalidClick(BotonLimitado fragment) { }
        default void onLastClick(BotonLimitado fragment) { }
        default void onFirstClick(BotonLimitado fragment) { }
        default void onSpecialClick(BotonLimitado fragment) { }
        default void onNumClick(BotonLimitado fragment, int num) { }
    }
}
