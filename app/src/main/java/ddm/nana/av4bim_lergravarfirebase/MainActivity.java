package ddm.nana.av4bim_lergravarfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
//Camila Devita Basaglia - SC3010058 e Nana de Souza Ekman Simões - SC3010414

    private DatabaseReference BD = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference restaurante = BD.child("restaurante");

    private EditText txtMesa;
    private EditText txtItem;
    private EditText txtProduto;
    private EditText txtPreco;
    private EditText txtMesaCoz;
    private EditText txtItemCoz;

    private Button btnInserir;
    private Button btnListar;
    private Button btnCalcular;
    private Button btnZerar;
    private Button btnAtender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMesa = findViewById( R.id.txtMesa );
        txtItem = findViewById( R.id.txtItem );
        txtProduto = findViewById( R.id.txtProduto );
        txtPreco = findViewById( R.id.txtPreco );
        txtMesaCoz = findViewById( R.id.txtMesaCoz );
        txtItemCoz = findViewById( R.id.txtItemCoz );

        btnInserir = findViewById( R.id.btnInserir );
        btnListar = findViewById( R.id.btnListar );
        btnCalcular = findViewById( R.id.btnCalcular );
        btnZerar = findViewById( R.id.btnZerar );
        btnAtender = findViewById( R.id.btnAtender );

        EscutadorBotoes b = new EscutadorBotoes();
        btnInserir.setOnClickListener( b );
        btnListar.setOnClickListener( b );
        btnCalcular.setOnClickListener( b );
        btnZerar.setOnClickListener( b );
        btnAtender.setOnClickListener( b );


    }

    private class EscutadorBotoes implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            Button b = (Button) view;
            String mesa;
            String item;
            String produto;
            double preco;

            switch (b.getId()){
                case R.id.btnInserir:

                    mesa = txtMesa.getText().toString();
                    item = txtItem.getText().toString();
                    produto = txtProduto.getText().toString();
                    preco = Double.parseDouble( txtPreco.getText().toString() );

                    Item i = new Item(produto, preco);
                    restaurante.child(mesa).child(item).setValue(i);

                    break;

                case R.id.btnListar:

                    mesa = txtMesa.getText().toString();
                    DatabaseReference itens = restaurante.child(mesa);
                    itens.addListenerForSingleValueEvent( new EscutadorFirebaseListar() );
                    txtMesa.setText("");

                    break;

                case R.id.btnCalcular:

                    mesa = txtMesa.getText().toString();
                    DatabaseReference conta = restaurante.child(mesa);
                    conta.addListenerForSingleValueEvent( new EscutadorFirebaseCalcular() );
                    txtMesa.setText("");

                    break;

                case R.id.btnZerar:

                    mesa = txtMesa.getText().toString();
                    DatabaseReference m = restaurante.child(mesa);
                    m.setValue(null);

                    break;

                case R.id.btnAtender:

                    mesa = txtMesaCoz.getText().toString();
                    item = txtItemCoz.getText().toString();
                    DatabaseReference pedido = restaurante.child(mesa).child(item);
                    pedido.addListenerForSingleValueEvent( new EscutadorFirebaseAtender() );

                    break;
            }

        }
    }

    private class EscutadorFirebaseListar implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                String produto;
                boolean atendido;
                double preco;

                for (DataSnapshot itens : dataSnapshot.getChildren()) {

                    Item i = itens.getValue(Item.class);

                    produto = i.getProduto();
                    atendido = i.isAtendido();
                    preco = i.getPreco();
                    Toast.makeText(MainActivity.this, "Produto: " + produto + "\nAtendido: " + atendido + "\nPreço: " + preco, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

    private class EscutadorFirebaseCalcular implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                double preco;
                double soma = 0;

                for (DataSnapshot itens : dataSnapshot.getChildren()) {

                    Item i = itens.getValue(Item.class);

                    preco = i.getPreco();
                    soma = soma + preco;
                }

                Toast.makeText(MainActivity.this, "Total da conta: " + soma, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

    private class EscutadorFirebaseAtender implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                Item i = dataSnapshot.getValue(Item.class);
                i.setAtendido(true);

                String mesa = txtMesaCoz.getText().toString();
                String item = txtItemCoz.getText().toString();
                restaurante.child(mesa).child(item).setValue(i);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

}