package com.example.studypomodoro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectsAdapter(private val projects: List<Project>) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project_card, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount() = projects.size

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val projectIcon: ImageView = itemView.findViewById(R.id.iv_project_icon)
        private val projectTaskCount: TextView = itemView.findViewById(R.id.tv_project_task_count)
        private val projectTitle: TextView = itemView.findViewById(R.id.tv_project_title)

        fun bind(project: Project) {
            projectIcon.setImageResource(project.icon)
            projectTaskCount.text = project.taskCount
            projectTitle.text = project.title
        }
    }
}

class TasksAdapter(
    private var tasks: MutableList<Task>,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_timeline, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, onTaskClick, position == 0, position == tasks.size - 1)
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskStatus: ImageView = itemView.findViewById(R.id.iv_task_status)
        private val taskName: TextView = itemView.findViewById(R.id.tv_task_name)
        private val taskProject: TextView = itemView.findViewById(R.id.tv_task_project)
        private val taskTime: TextView = itemView.findViewById(R.id.tv_task_time)
        private val timelineTop: View = itemView.findViewById(R.id.timeline_line_top)
        private val timelineBottom: View = itemView.findViewById(R.id.timeline_line_bottom)

        fun bind(task: Task, onTaskClick: (Task) -> Unit, isFirst: Boolean, isLast: Boolean) {
            taskStatus.setImageResource(task.status)
            taskName.text = task.name
            taskProject.text = task.projectName
            taskTime.text = task.time
            itemView.setOnClickListener { onTaskClick(task) }

            timelineTop.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
            timelineBottom.visibility = if (isLast) View.INVISIBLE else View.VISIBLE
        }
    }
}