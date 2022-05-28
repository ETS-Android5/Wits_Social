package com.example.witssocial.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.witssocial.Model.Post;
import com.example.witssocial.Model.User;
import com.example.witssocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private final PostRecyclerViewInterface postRecyclerViewInterface;
    Context context;
    ArrayList<Post> list;
    DatabaseReference database, postsRef,userRef;





    private static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public PostAdapter(Context context, ArrayList<Post> list, PostRecyclerViewInterface postRecyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.postRecyclerViewInterface = postRecyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view, postRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = list.get(position);
        holder.username.setText(post.getUsername());
        holder.caption.setText(post.getCaption());
        Glide.with(holder.itemView).load(list.get(position).getImage()).into(holder.post_image);


        //setProfile picture for each user

        database = FirebaseDatabase.getInstance().getReference("Users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String postUsername = post.getUsername();
                    User user = dataSnapshot.getValue(User.class);

                    if( user.getUsername().equals(postUsername))
                    {

                        Glide.with(holder.itemView).load(user.getImageurl()).into(holder.profile_picture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView username, caption, likes;
        ImageView post_image, like, profile_picture;

       public ViewHolder(@NonNull View itemView, PostRecyclerViewInterface postRecyclerViewInterface) {
           super(itemView);

           username = itemView.findViewById(R.id.username);
           caption = itemView.findViewById(R.id.caption);
           post_image = itemView.findViewById(R.id.post_image);
           post_image.setClipToOutline(true);
           like = itemView.findViewById(R.id.like);
           likes = itemView.findViewById(R.id.likes);
           profile_picture = itemView.findViewById(R.id.image_profile);


           //Attach onclick lister to the item view
           username.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(postRecyclerViewInterface != null){
                        int position = getBindingAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            postRecyclerViewInterface.onUsernameClick(position);
                        }
                   }
               }
           });
//This  method isLikes checks if an image has been liked by a user  or not
           isLikes(Post.getPostid(),like);
//           Now check number of likes
           numberOfLikes(likes,Post.getPostid());
//Now when clickin the like button,  the post  gets liked or not
           like.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(like.getTag().equals("liked"))
                   {
                       FirebaseDatabase.getInstance().getReference().child("Posts").child(firebaseUser.getUid()).setValue(true);

                       like.setImageResource(R.drawable.ic_baseline_favorite_24);
                       likes.setTextColor(Color.parseColor("#FF0000"));

                   }else{
                       FirebaseDatabase.getInstance().getReference().child("Posts").child(firebaseUser.getUid()).removeValue();
                   }

               }
           });
       }

        private void numberOfLikes(TextView likes, String postid) {
           FirebaseDatabase.getInstance().getReference("Likes");
           DatabaseReference likesREF  =  FirebaseDatabase.getInstance().getReference("User_post").child("Likes").child(Post.getPostid());
            likesREF.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        long n = snapshot.getChildrenCount();
                        int s = Integer.parseInt(String.valueOf(n));
                        likes.setText(s+" likes");
                    }
                    else {
                        like.setImageResource(R.drawable.ic_like);
                        likes.setTextColor(Color.parseColor("#808080"));
                        like.setTag("like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
       }

        private void isLikes(String postid, ImageView like) {
       final DatabaseReference likesREF  =  FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
            likesREF.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.child(firebaseUser.getUid()).exists())
               {
                   like.setImageResource(R.drawable.ic_baseline_favorite_24);
                   likes.setTextColor(Color.parseColor("#FF0000"));
                   like.setTag("liked");
               }
               else {
                   like.setImageResource(R.drawable.ic_like);
                   likes.setTextColor(Color.parseColor("#808080"));
                   like.setTag("like");
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

        }
    }

}
