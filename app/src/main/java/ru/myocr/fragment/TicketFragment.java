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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.clans.fab.FloatingActionMenu;

import nl.littlerobots.cupboard.tools.provider.UriHelper;
import ru.myocr.R;
import ru.myocr.activity.MainActivity;
import ru.myocr.activity.TicketActivity;
import ru.myocr.db.ReceiptContentProvider;
import ru.myocr.model.DummyReceipt;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static ru.myocr.activity.TicketActivity.ARG_RECEIPT;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link TicketFragmentInteractionListener}
 * interface.
 */
public class TicketFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_QUERY = "Tag";
    private TicketRecyclerViewAdapter adapter;
    private FloatingActionMenu fab;

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
        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        fab = (FloatingActionMenu) view.findViewById(R.id.floatingMenu);
        fab.hideMenu(false);

        view.findViewById(R.id.fabCam).setOnClickListener(v -> {
            fab.hideMenu(true);
            ((MainActivity) getActivity()).onClickRunCamera();
        });
        view.findViewById(R.id.fabGallery).setOnClickListener(v -> {
            fab.hideMenu(true);
            ((MainActivity) getActivity()).onClickAddGallery();
        });
        view.findViewById(R.id.fabCamScanner).setOnClickListener(v -> {
            fab.hideMenu(true);
            ((MainActivity) getActivity()).onClickRunCamScanner();
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new TicketRecyclerViewAdapter(getActivity(), null, new TicketFragmentInteractionListener() {
            @Override
            public void onClickTicketItem(Receipt item) {
                TicketFragment.this.onClickTicketItem(item);
            }

            @Override
            public void onLongClickTicketItem(Receipt item) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Вы действительно хотите удалить этот чек?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            cupboard().withContext(getActivity())
                                    .delete(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(Receipt.class), item);
                            cupboard().withContext(getActivity())
                                    .delete(UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(ReceiptItem.class),
                                            "receiptId = ?", item._id.toString());
                        }).show();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
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
        fab.postDelayed(() -> fab.showMenu(true), 200);
    }

    private void onClickTicketItem(Receipt item) {
        Intent intent = new Intent(getActivity(), TicketActivity.class);
        intent.putExtra(ARG_RECEIPT, item._id);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (0 == id) {
            return new CursorLoader(getActivity(),
                    UriHelper.with(ReceiptContentProvider.AUTHORITY).getUri(Receipt.class),
                    null, null, null, "date DESC");
        } else if (1 == id) {
            return new CursorLoader(getActivity(),
                    ReceiptContentProvider.URI_RECEIPT_SEARCH,
                    null, null, new String[]{args.getString(KEY_QUERY)}, null);
        } else {
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ticket_fragment, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateQuery(query);
                searchView.clearFocus();
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateQuery(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(this::showAll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_fake_receipt) {
            DummyReceipt.addToDb();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateQuery(String query) {
        Bundle arg = new Bundle();
        arg.putString(KEY_QUERY, query);
        getLoaderManager().restartLoader(1, arg, TicketFragment.this);
    }

    private boolean showAll() {
        getLoaderManager().restartLoader(0, null, TicketFragment.this);
        return false;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 0 && data.getCount() == 0) {
            DummyReceipt.addToDb();
            DummyReceipt.addToDb();
            for (int i = 0; i < 10; i++) {
                DummyReceipt.addToDb();
            }
        }
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
