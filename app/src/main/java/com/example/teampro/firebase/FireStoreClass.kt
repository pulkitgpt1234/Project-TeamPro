package com.example.teampro.firebase

import android.app.Activity
import android.bluetooth.BluetoothAssignedNumbers
import android.util.Log
import android.widget.Toast
import com.example.teampro.activities.*
import com.example.teampro.models.Board
import com.example.teampro.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.example.teampro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity,userInfo: User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegisteredSuccess()
                }.addOnFailureListener{
                    e->
                    Log.e(activity.javaClass.simpleName,"Error writing document",e)
                }
    }

    fun createBoard(activity:CreateBoardActivity,board: Board){
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge())
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,"Board Created Successfully")

                    Toast.makeText(activity, "Board Created Successfully", Toast.LENGTH_SHORT).show()
                    activity.boardCreatedSuccessfully()
                }.addOnFailureListener {
                    exception ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while Creating a Board",exception)
                }

    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
                .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
                .get().addOnSuccessListener {
                    document->
                    Log.i(activity.javaClass.simpleName,document.documents.toString())
                    val boardList: ArrayList<Board> = ArrayList()
                    for(i in document.documents){
                        val board= i.toObject(Board::class.java)!!
                        board.documentId = i.id
                        boardList.add(board)
                    }
                    activity.populateBoardsListToUI(boardList)
                }.addOnFailureListener {
                    e->
                    Log.e(activity.javaClass.simpleName,"Error While Creating Board",e)
                }
    }

    fun addUpdateTaskList(activity:Activity,board:Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap)
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,"TaskList updated Successfully")
                    if(activity is TaskListActivity)
                        activity.addUpdateTaskListSuccess()
                    else if(activity is CardDetailsActivity)
                        activity.addUpdateTaskListSuccess()
                }.addOnFailureListener {
                    exception ->
                if(activity is TaskListActivity)
                        activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"error while creating a board",exception)
                }
    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .update(userHashMap).addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data updated Successfully!")
                Toast.makeText(activity,"Profile Updated Successfully!",Toast.LENGTH_SHORT).show()
                when(activity) {
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity->{
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                e->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error while Creating a Board",e)
                Toast.makeText(activity, "Error while Creating a Board", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity,readBoardsList:Boolean = false) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
                .get()
                .addOnSuccessListener {document->
                    val loggedInUser = document.toObject(User::class.java)!!
                    when(activity){
                        is SignInActivity->{
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity->{
                            activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
                        }
                        is MyProfileActivity->{
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }

                }.addOnFailureListener {
                    e->
                    when(activity){
                        is SignInActivity->{
                            activity.hideProgressDialog()
                        }
                        is MainActivity->{
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e("SignInUser","Error Signing In",e)
                }
    }

    fun getCurrentUserId(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID =""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get().addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)
            }.addOnFailureListener {
                    e->
                Log.e(activity.javaClass.simpleName,"Error While Creating Board",e)
            }
    }

    fun getAssignedMembersListDetails(activity: Activity,assignedTo:ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo).get()
            .addOnSuccessListener {
                document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())

                val usersList:ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                if(activity is MembersActivity)
                    activity.setupMembersList(usersList)
                else if(activity is TaskListActivity)
                    activity.boardMembersDetailsList(usersList)
            }.addOnFailureListener {
                e->
                    if(activity is MembersActivity)
                        activity.hideProgressDialog()
                    else if(activity is TaskListActivity)
                        activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"error while creating a member list",e)
            }
    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFireStore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL,email).get()
                .addOnSuccessListener {
                    document->if(document.documents.size>0){
                        val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
                }.addOnFailureListener {
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while Creating Details")
                }
    }

    fun assignMemberToBoard(activity: MembersActivity,board:Board,user:User){
            val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS)
                .document(board.documentId).update(assignedToHashMap)
                .addOnSuccessListener {
                    activity.memberAssignSuccess(user)
                }.addOnFailureListener {
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error While Creating a Board")
                }
    }
}