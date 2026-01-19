import numpy as np
import matplotlib.pyplot as plt


labels = ['GD', 'SGD', 'ADAM']

def plot_multiple_datasets(y_value_files, time_files, labels, x_label, y_label, title, x_min, x_max, ax):

    datasets = []

    # Load all y-value datasets and their corresponding finish times
    for y_file, time_file in zip(y_value_files, time_files):
        # Read y-values
        with open(y_file, 'r') as yf:
            y_values = np.array([float(line.strip()) for line in yf])
            n_points = len(y_values)
        
        # Read finish time
        with open(time_file, 'r') as tf:
            finish_time = float(tf.read().strip())
        
        # Generate x-values based on finish time
        x_values = np.linspace(0, finish_time, n_points)
        datasets.append((x_values, y_values))

    # Plot the data on the given axes (ax)
    for (x_values, y_values), label in zip(datasets, labels):
        ax.plot(x_values, y_values, label=label, lw=1)

    # Set x and y axis limits
    ax.set_xlim(x_min, x_max)

    # Add labels and title
    ax.set_xlabel(x_label)
    ax.set_ylabel(y_label)
    ax.set_title(title)

    # Add a legend
    ax.legend()

    # Add grid
    ax.grid()

def plot_all_graphs():
    # Define the y-value and x-value files, and labels for each dataset
    y_values_train = ['Bkismi/W6/gd/success_train.txt', 'Bkismi/W6/sgd/success_train.txt', 'Bkismi/W6/adam/success_train.txt']
    x_values_epoch = ['Bkismi/W6/gd/epoch.txt', 'Bkismi/W6/sgd/epoch.txt', 'Bkismi/W6/adam/epoch.txt']

    y_values_test = ['Bkismi/W6/gd/success_test.txt', 'Bkismi/W6/sgd/success_test.txt', 'Bkismi/W6/adam/success_test.txt']
    x_values_epoch_test = ['Bkismi/W6/gd/epoch.txt', 'Bkismi/W6/sgd/epoch.txt', 'Bkismi/W6/adam/epoch.txt']

    y_values_loss_train = ['Bkismi/W6/gd/loss_train.txt', 'Bkismi/W6/sgd/loss_train.txt', 'Bkismi/W6/adam/loss_train.txt']
    x_values_epoch_loss_train = ['Bkismi/W6/gd/epoch.txt', 'Bkismi/W6/sgd/epoch.txt', 'Bkismi/W6/adam/epoch.txt']

    y_values_loss_test = ['Bkismi/W6/gd/loss_test.txt', 'Bkismi/W6/sgd/loss_test.txt', 'Bkismi/W6/adam/loss_test.txt']
    x_values_epoch_loss_test = ['Bkismi/W6/gd/epoch.txt', 'Bkismi/W6/sgd/epoch.txt', 'Bkismi/W6/adam/epoch.txt']

    y_values_train_time = ['Bkismi/W6/gd/success_train.txt', 'Bkismi/W6/sgd/success_train.txt', 'Bkismi/W6/adam/success_train.txt']
    x_values_time = ['Bkismi/W6/gd/time.txt', 'Bkismi/W6/sgd/time.txt', 'Bkismi/W6/adam/time.txt']

    y_values_test_time = ['Bkismi/W6/gd/success_test.txt', 'Bkismi/W6/sgd/success_test.txt', 'Bkismi/W6/adam/success_test.txt']
    x_values_time_test = ['Bkismi/W6/gd/time.txt', 'Bkismi/W6/sgd/time.txt', 'Bkismi/W6/adam/time.txt']

    y_values_loss_train_time = ['Bkismi/W6/gd/loss_train.txt', 'Bkismi/W6/sgd/loss_train.txt', 'Bkismi/W6/adam/loss_train.txt']
    x_values_time_loss_train = ['Bkismi/W6/gd/time.txt', 'Bkismi/W6/sgd/time.txt', 'Bkismi/W6/adam/time.txt']

    y_values_loss_test_time = ['Bkismi/W6/gd/loss_test.txt', 'Bkismi/W6/sgd/loss_test.txt', 'Bkismi/W6/adam/loss_test.txt']
    x_values_time_loss_test = ['Bkismi/W6/gd/time.txt', 'Bkismi/W6/sgd/time.txt', 'Bkismi/W6/adam/time.txt']

    # Create the subplot grid (2 rows, 4 columns)
    fig, axs = plt.subplots(2, 4, figsize=(20, 12))

    # Plot each graph on a different subplot
    plot_multiple_datasets(y_values_train, x_values_epoch, labels, "iteration", "success rate (%)", "W6 Train data success rate vs iteration", -2, 3502, axs[0, 0])
    plot_multiple_datasets(y_values_test, x_values_epoch_test, labels, "iteration", "success rate (%)", "W6 Test data success rate vs iteration", -2, 3502, axs[0, 1])
    plot_multiple_datasets(y_values_loss_train, x_values_epoch_loss_train, labels, "iteration", "Mean Square Error * 100", "W6 Train data Mean Square Error vs iteration", -2, 3502, axs[0, 2])
    plot_multiple_datasets(y_values_loss_test, x_values_epoch_loss_test, labels, "iteration", "Mean Square Error * 100", "W6 Test data Mean Square Error vs iteration", -2, 3502, axs[0, 3])

    plot_multiple_datasets(y_values_train_time, x_values_time, labels, "time (s)", "success rate (%)", "W6 Train data success rate vs time", -0.5, 18, axs[1, 0])
    plot_multiple_datasets(y_values_test_time, x_values_time_test, labels, "time (s)", "success rate (%)", "W6 Test data success rate vs time", -0.5, 18, axs[1, 1])
    plot_multiple_datasets(y_values_loss_train_time, x_values_time_loss_train, labels, "time (s)", "Mean Square Error * 100", "W6 Train data Mean Square Error vs time", -0.5, 18, axs[1, 2])
    plot_multiple_datasets(y_values_loss_test_time, x_values_time_loss_test, labels, "time (s)", "Mean Square Error * 100", "W6 Test data Mean Square Error vs time", -0.5, 18, axs[1, 3])

    # Adjust layout to prevent overlapping
    plt.tight_layout()
    
    plt.savefig('Bkismi/fotolar/1. kisim/graphs_W6.png', dpi=300)

    # Show the combined plot
    # plt.show()

# Call the function to plot all graphs
plot_all_graphs()
