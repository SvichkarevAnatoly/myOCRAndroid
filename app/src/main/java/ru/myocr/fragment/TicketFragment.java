package ru.myocr.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.api.ApiHelper;
import ru.myocr.model.Receipt;
import ru.myocr.test.R;
import rx.Subscription;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link TicketFragmentInteractionListener}
 * interface.
 */
public class TicketFragment extends Fragment {

    private TicketRecyclerViewAdapter adapter;
    private List<Receipt> tickets = new ArrayList<>();
    private Subscription subscription;

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
        subscription = ApiHelper.makeApiRequest(null, ApiHelper::getAllReceipt, throwable -> {
                },
                getTicketResponses -> {
                    tickets.clear();
                    tickets.addAll(getTicketResponses);
                    adapter.notifyDataSetChanged();
                }, null);
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
            adapter = new TicketRecyclerViewAdapter(tickets, this::onClickTicketItem);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


    private void onClickTicketItem(Receipt item) {
        String[] strItems = new String[item.items.size()];
        for (int i = 0; i < item.items.size(); i++) {
            strItems[i] = item.items.get(i).toString();
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(item.market.title)
                .setItems(strItems, (dialog, which) -> {
                })
                .show();
    }

    public interface TicketFragmentInteractionListener {
        void onClickTicketItem(Receipt item);
    }
}
