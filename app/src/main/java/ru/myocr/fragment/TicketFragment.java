package ru.myocr.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.R;
import ru.myocr.activity.TicketActivity;
import ru.myocr.db.ReceiptContentProvider;
import ru.myocr.model.Receipt;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static ru.myocr.activity.TicketActivity.ARG_RECEIPT;
import static ru.myocr.model.DummyReceipt.addToDb;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link TicketFragmentInteractionListener}
 * interface.
 */
public class TicketFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TicketRecyclerViewAdapter adapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TicketFragment() {
    }

    public static TicketFragment newInstance() {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addToDb();
        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new TicketRecyclerViewAdapter(getActivity(), null, new TicketFragmentInteractionListener() {
                @Override
                public void onClickTicketItem(Receipt item) {
                    TicketFragment.this.onClickTicketItem(item);
                }

                @Override
                public void onLongClickTicketItem(Receipt item) {
                    cupboard().withContext(getActivity())
                            .delete(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(Receipt.class),
                                    item);
                }
            });
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Мои чеки");
    }

    private void onClickTicketItem(Receipt item) {
        Intent intent = new Intent(getActivity(), TicketActivity.class);
        intent.putExtra(ARG_RECEIPT, item._id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(Receipt.class),
                null, null, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ticket_fragment, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    public interface TicketFragmentInteractionListener {
        void onClickTicketItem(Receipt item);

        void onLongClickTicketItem(Receipt item);
    }
}
