package com.example.caloripucp.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Beans.Elemento;
import com.example.caloripucp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ComidaBottomSheetDialog extends BottomSheetDialogFragment {

    TextInputEditText nombreEditText;
    TextInputEditText caloriasEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_comida, container, false);

        nombreEditText = view.findViewById(R.id.field_nombre_comida);
        caloriasEditText = view.findViewById(R.id.field_caloria_comida);

        view.findViewById(R.id.btn_confirm_comida).setOnClickListener(v -> {

            if(validarCampos(view)){
                String nombre = nombreEditText.getText().toString();
                int calorias = Integer.parseInt(caloriasEditText.getText().toString());
                Elemento nuevoElemento = new Elemento();
                nuevoElemento.setNombre(nombre);
                nuevoElemento.setCalorias(calorias);
                nuevoElemento.setTipo("Comida");
                nuevoElemento.setearHora();

                // Agreganmos el elemento a través de la actividad principal
                AppActivity appActivity = (AppActivity) getActivity();
                if (appActivity != null) {
                    appActivity.agregarElementoRegistroDiario(nuevoElemento);
                }
                dismiss();
            }
        });

        return view;
    }

    public void quitarErrores(View view){
        ((TextInputLayout)view.findViewById(R.id.field_nombre_comida_layout)).setError(null);
        ((TextInputLayout)view.findViewById(R.id.field_calorias_comida_layout)).setError(null);
    }

    public boolean validarCampos(View view){
        boolean validacion = true;
        quitarErrores(view);
        if(nombreEditText.getText().toString().isEmpty()){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_nombre_comida_layout)).setError("El campo de nombre no puede estar vacío!");
        }
        if(caloriasEditText.getText().toString().isEmpty()){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_calorias_comida_layout)).setError("El campo de calorías no puede estar vacío!");
        } else if(Integer.parseInt(caloriasEditText.getText().toString()) == 0){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_calorias_comida_layout)).setError("Las calorías no pueden ser 0 Kcal!");
        }

        return validacion;
    }
}

