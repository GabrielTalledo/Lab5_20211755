package com.example.caloripucp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.caloripucp.Beans.Elemento;
import com.example.caloripucp.Beans.Perfil;
import com.example.caloripucp.Beans.Registro;
import com.example.caloripucp.Fragments.DiarioFragment;
import com.example.caloripucp.Fragments.HistorialFragment;
import com.example.caloripucp.Fragments.PerfilFragment;
import com.example.caloripucp.R;
import com.example.caloripucp.Tools.Almacenamiento;
import com.example.caloripucp.databinding.ActivityAppBinding;
import com.example.caloripucp.databinding.ActivityInicioBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class AppActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    ActivityAppBinding binding;
    BottomNavigationView bottomNav;
    private MutableLiveData<Registro> registroDiarioLiveData;
    private MutableLiveData<Perfil> perfilLiveData;

    // Getter y setter:
    public ActivityAppBinding getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding:
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // LiveData:
        registroDiarioLiveData = new MutableLiveData<>(Almacenamiento.obtenerRegistroDiario(this));
        perfilLiveData = new MutableLiveData<>(Almacenamiento.obtenerPerfilInicial(this));

        // Bottom menu:
        bottomNav = binding.bottomNavigation;
        bottomNav.setOnNavigationItemSelectedListener(this);
        bottomNav.setSelectedItemId(R.id.registro_diario);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new DiarioFragment()).commit();
        }

        // Gestionamos intent de la notificacion de motivacion:
        Log.d("ASDADASD", "onCreate: "+getIntent().getStringExtra("motivacionFragment"));
        if(getIntent().getStringExtra("motivacionFragment") != null){
            bottomNav.setSelectedItemId(R.id.perfil);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new PerfilFragment()).commit();
        }

    }


    // ---------
    //  MÉTODOS:
    // ---------

    // Registro:

    public LiveData<Registro> getRegistroDiarioLiveData() {
        return registroDiarioLiveData;
    }

    public void agregarElementoRegistroDiario(Elemento elemento) {
        Registro registro = registroDiarioLiveData.getValue();
        if (registro != null) {
            registro.getElementos().add(elemento);

            registroDiarioLiveData.setValue(registro); // Actualiza el LiveData
        }
        // Actualizamos en el almacenamiento:
        Almacenamiento.guardarRegistroDiario(registro,this);
        Snackbar.make(binding.getRoot(), "Elemento agregado al registro diario!", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_navigation).show();
    }

    public void agregarRegistroDiario(Registro registro) {
        registroDiarioLiveData.setValue(registro);
        // Actualizamos en el almacenamiento:
        Almacenamiento.guardarRegistroDiario(registro,this);
        Snackbar.make(binding.getRoot(), "Nuevo día para registrar!", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_navigation).show();
    }

    public void eliminarElementoRegistroDiario(int posicion) {
        Registro registro = registroDiarioLiveData.getValue();
        if (registro != null) {
            registro.getElementos().remove(posicion);
            registroDiarioLiveData.setValue(registro); // Actualiza el LiveData
        }
        // Actualizamos en el almacenamiento:
        Almacenamiento.guardarRegistroDiario(registro,this);
        Snackbar.make(binding.getRoot(), "Elemento eliminado del registro diario!", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_navigation).show();
    }

    // Perfil:

    public LiveData<Perfil> getPerfilLiveData() {
        return perfilLiveData;
    }

    public void actualizarPerfil(Perfil perfil) {
        perfilLiveData.setValue(perfil);
        // Actualizamos en el almacenamiento:
        Almacenamiento.guardarPerfilInicial(perfil,this);
        Snackbar.make(binding.getRoot(), "Perfil actualizado!", Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_navigation).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        Fragment fragmentoSeleccionado = null;

        if(item.getItemId() == R.id.registro_diario){
            fragmentoSeleccionado = new DiarioFragment();
        }
        if(item.getItemId() == R.id.registro_historial){
            fragmentoSeleccionado = new HistorialFragment();
        }
        if(item.getItemId() == R.id.perfil){
            fragmentoSeleccionado = new PerfilFragment();
        }

        if(item.getItemId() != bottomNav.getSelectedItemId()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, fragmentoSeleccionado).commit();
        }

        return true;
    }
}