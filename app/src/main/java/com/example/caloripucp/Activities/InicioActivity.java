package com.example.caloripucp.Activities;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.caloripucp.Beans.Catalogo;
import com.example.caloripucp.Beans.Elemento;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Beans.RegistroHistorial;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.example.caloripucp.databinding.ActivityInicioBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    ActivityInicioBinding binding;
    Perfil perfil;
    Catalogo catalogo;
    RegistroHistorial historial;
    Button btn_ingresar;
    TextInputEditText field_nombre;
    TextInputEditText field_peso;
    TextInputEditText field_altura;
    TextInputEditText field_edad;
    AutoCompleteTextView field_genero;
    AutoCompleteTextView field_actividad;
    AutoCompleteTextView field_objetivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding:
        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ya cuenta con un perfil?

        if(Almacenamiento.obtenerPerfilInicial(this) != null){
            startActivity(new Intent(this,AppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            supportFinishAfterTransition();
        }

        // Lógica:
        perfil = new Perfil();
        catalogo = new Catalogo();
        catalogo.setCatalogoAlimentos(crearCatalogoAlimentos());
        historial = new RegistroHistorial();

        // Inputs
        btn_ingresar = binding.btnIngresar;
        field_nombre = binding.fieldNombre;
        field_peso = binding.fieldPeso;
        field_altura = binding.fieldAltura;
        field_edad = binding.fieldEdad;
        field_genero = binding.fieldGenero;
        field_actividad = binding.fieldActividad;
        field_objetivo = binding.fieldObjetivo;

        String[] generos = new String[]{"Hombre", "Mujer"};
        String[] actividades = new String[]{"Sedentario", "Poco Activo", "Activo", "Muy Activo", "Super Activo"};
        String[] objetivos = new String[]{"Perder Peso", "Mantener Peso", "Ganar Peso"};

        ArrayAdapter<String> adapter_genero = new ArrayAdapter<>(this, R.layout.item_dropdown, generos);
        ArrayAdapter<String> adapter_actividad = new ArrayAdapter<>(this, R.layout.item_dropdown, actividades);
        ArrayAdapter<String> adapter_objetivos = new ArrayAdapter<>(this, R.layout.item_dropdown, objetivos);

        field_genero.setAdapter(adapter_genero);
        field_actividad.setAdapter(adapter_actividad);
        field_objetivo.setAdapter(adapter_objetivos);

        field_genero.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                perfil.setGenero("Hombre");
            }else{
                perfil.setGenero("Mujer");
            }
        });

        field_actividad.setOnItemClickListener((parent, view, position, id) -> {
            Double[] factores = new Double[]{1.2, 1.375, 1.55, 1.725, 1.9};
            perfil.setActividad(actividades[position]);
            perfil.setActividadFactor(factores[position]);
        });

        field_objetivo.setOnItemClickListener((parent, view, position, id) -> {
            Double[] caloriasFactor = new Double[]{-300.0, 0.0, 500.0};
            perfil.setObjetivo(objetivos[position]);
            perfil.setObjetivoCalorias(caloriasFactor[position]);
        });

        // Botón Ingresar:

        btn_ingresar.setOnClickListener(view -> {
            if(validarCampos()){
                // Seteamos el perfil inicial:
                perfil.setNombre(field_nombre.getText().toString());
                perfil.setPeso(Double.parseDouble(field_peso.getText().toString()));
                perfil.setAltura(Double.parseDouble(field_altura.getText().toString()));
                perfil.setEdad(Double.parseDouble(field_edad.getText().toString()));
                perfil.setObjetivoCaloriasDiarias(calcularCaloriasDiarias());
                perfil.setIntervaloMotivacionNoti(30);
                Log.d("TAG", ""+perfil.getObjetivoCaloriasDiarias());
                // Creamos el archivo del perfil y guardamos el objeto:
                Almacenamiento.guardarPerfilInicial(perfil, this);
                Perfil perfilDeAlmacenamiento = Almacenamiento.obtenerPerfilInicial(this);
                Log.d("TAG", "Nombre: "+perfilDeAlmacenamiento.getNombre());
                Log.d("TAG", "Calorias Diarias: "+perfilDeAlmacenamiento.getObjetivoCaloriasDiarias());
                // Creamos el primer registro (el registro actual):
                Registro registroActual = new Registro();
                registroActual.setearInformacionDia(1);
                // Guardamos este primer registro:
                Almacenamiento.guardarRegistroDiario(registroActual, this);
                Registro registroDeAlmacenamiento = Almacenamiento.obtenerRegistroDiario(this);
                // Guardamos el catalogo:
                Almacenamiento.guardarCatalogo(catalogo,this);
                Catalogo xd = Almacenamiento.obtenerCatalogo(this);
                Log.d("XDDDDDDD", "onCreate: " + xd.getCatalogoAlimentos().get(1).getNombre());
                // Guardamos el historial:
                Almacenamiento.guardarHistorial(historial,this);
                // Pasamos a la app:
                startActivity(new Intent(this,AppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                supportFinishAfterTransition();
            }
        });

    }


    // ---------
    //  MÉTODOS:
    // ---------

    public ArrayList<Elemento> crearCatalogoAlimentos() {
        ArrayList<Elemento> alimentos = new ArrayList<>();

        // Agregamos 5 alimentos de ejemplo
        alimentos.add(new Elemento("Manzana", 115, "Comida"));
        alimentos.add(new Elemento("Avena", 389, "Comida"));
        alimentos.add(new Elemento("Salmón", 206, "Comida"));
        alimentos.add(new Elemento("Ensalada", 190, "Comida"));
        alimentos.add(new Elemento("Palta", 320, "Comida"));

        return alimentos;
    }

    public int calcularCaloriasDiarias(){
        double caloriasDiarias;

        if(perfil.getGenero().equals("Hombre")){
            caloriasDiarias = (66.47 + (13.75 * perfil.getPeso()) + (5.0 * perfil.getAltura()) - (6.74 * perfil.getEdad()))*perfil.getActividadFactor() + perfil.getObjetivoCalorias();
        }else {
            caloriasDiarias = (655.1 + (9.56 * perfil.getPeso()) + (1.85 * perfil.getAltura()) - (4.68 * perfil.getEdad()))*perfil.getActividadFactor() + perfil.getObjetivoCalorias();
        }

        return (int) caloriasDiarias;
    }

    public void quitarErrores(){
        binding.fieldNombreLayout.setError(null);
        binding.fieldPesoLayout.setError(null);
        binding.fieldAlturaLayout.setError(null);
        binding.fieldEdadLayout.setError(null);
        binding.fieldGeneroLayout.setError(null);
        binding.fieldActividadLayout.setError(null);
        binding.fieldObjetivoLayout.setError(null);
    }

    public boolean validarCampos(){
        boolean validacion = true;
        quitarErrores();
        if(field_nombre.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldNombreLayout.setError("El campo no puede estar vacío!");
        }
        if(field_peso.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldPesoLayout.setError("El campo no puede estar vacío!");
        } else if(Double.parseDouble(field_peso.getText().toString()) == 0){
            validacion = false;
            binding.fieldPesoLayout.setError("El peso no puede ser 0 Kg!");
        }
        if(field_altura.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldAlturaLayout.setError("El campo no puede estar vacío!");
        }else if(Double.parseDouble(field_altura.getText().toString()) == 0){
            validacion = false;
            binding.fieldAlturaLayout.setError("La altura no puede ser 0 cm!");
        }
        if(field_edad.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldEdadLayout.setError("El campo no puede estar vacío!");
        }else if(Double.parseDouble(field_edad.getText().toString()) == 0){
            validacion = false;
            binding.fieldEdadLayout.setError("La edad no puede ser 0 años!");
        }
        if(field_genero.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldGeneroLayout.setError("Debe elegir un género!");
        }
        if(field_actividad.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldActividadLayout.setError("Debe elegir su nivel de actividad!");
        }
        if(field_objetivo.getText().toString().isEmpty()){
            validacion = false;
            binding.fieldObjetivoLayout.setError("Debe elegir un pbjetivo!");
        }

        return validacion;
    }

}