package com.example.caloripucp.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloripucp.Activities.AppActivity;
import com.example.caloripucp.Beans.Catalogo;
import com.example.caloripucp.Beans.Elemento;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CatalogoBottomSheetDialog extends BottomSheetDialogFragment {

    TextInputEditText nombreEditText;
    TextInputEditText caloriasEditText;
    Elemento elementoElegido;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_catalogo, container, false);

        // Listado del catálogo:

        Catalogo catalogo = Almacenamiento.obtenerCatalogo((AppCompatActivity)getActivity());
        String[] alimentosCatalogo = catalogo.obtenerNombresCatalogo();

        ArrayAdapter<String> adapter_catalogo = new ArrayAdapter<>(getContext(),R.layout.item_dropdown, alimentosCatalogo);

        AutoCompleteTextView field_elemento_catalogo = view.findViewById(R.id.field_elemento_catalogo);
        field_elemento_catalogo.setAdapter(adapter_catalogo);
        TextInputLayout field_elemento_catalogo_layout = view.findViewById(R.id.field_elemento_catalogo_layout);

        field_elemento_catalogo.setOnItemClickListener((parent, view1, position, id) -> {
            elementoElegido = catalogo.getCatalogoAlimentos().get(position);
            elementoElegido.setearHora();
        });

        view.findViewById(R.id.btn_registro_catalogo).setOnClickListener(v -> {

            if(!field_elemento_catalogo.getText().toString().isEmpty()){
                // Agregamos el elemento al registro
                if(getActivity() != null){
                    ((AppActivity) getActivity()).agregarElementoRegistroDiario(elementoElegido);
                    dismiss();
                }
            }else{
                field_elemento_catalogo_layout.setError("Debe elegir un alimento del catálogo!");
            }
        });

        // Añadir nuevo elemento al catalogo:

        nombreEditText = view.findViewById(R.id.field_nombre_catalogo);
        caloriasEditText = view.findViewById(R.id.field_caloria_catalogo);

        view.findViewById(R.id.btn_confirm_catalogo).setOnClickListener(v -> {

            if(validarCampos(view)){
                String nombre = nombreEditText.getText().toString();
                int calorias = Integer.parseInt(caloriasEditText.getText().toString());
                Elemento nuevoElemento = new Elemento();
                nuevoElemento.setNombre(nombre);
                nuevoElemento.setCalorias(calorias);
                nuevoElemento.setTipo("Comida");

                // Agregamos el elemento al catalogo

                Catalogo catalogoNuevo = Almacenamiento.obtenerCatalogo((AppCompatActivity) getActivity());
                catalogoNuevo.getCatalogoAlimentos().add(nuevoElemento);
                Almacenamiento.guardarCatalogo(catalogoNuevo, (AppCompatActivity) getActivity());

                Snackbar.make(((AppActivity) getActivity()).getBinding().getRoot(), "Elemento añadido al catálogo!", Snackbar.LENGTH_SHORT).show();

                dismiss();
            }
        });

        return view;
    }

    public void quitarErrores(View view){
        ((TextInputLayout)view.findViewById(R.id.field_nombre_catalogo_layout)).setError(null);
        ((TextInputLayout)view.findViewById(R.id.field_calorias_catalogo_layout)).setError(null);
    }

    public boolean validarCampos(View view){
        boolean validacion = true;
        quitarErrores(view);
        if(nombreEditText.getText().toString().isEmpty()){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_nombre_catalogo_layout)).setError("El campo de nombre no puede estar vacío!");
        }
        if(caloriasEditText.getText().toString().isEmpty()){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_calorias_catalogo_layout)).setError("El campo de calorías no puede estar vacío!");
        } else if(Integer.parseInt(caloriasEditText.getText().toString()) == 0){
            validacion = false;
            ((TextInputLayout)view.findViewById(R.id.field_calorias_catalogo_layout)).setError("Las calorías no pueden ser 0 Kcal!");
        }

        return validacion;
    }
}

